package com.example.scoring.logic;

import com.example.scoring.domain.model.PostInteractionStats;
import com.example.scoring.domain.model.PostScoreBucket;
import com.example.scoring.domain.repo.BucketRepository;
import com.example.scoring.domain.repo.PostInteractionStatsRepository;
import com.example.shared.interaction.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class ScoringService
{
    public final static Long WATCH_SCORE_INTERVAL = 1000L;
    public final static Long CLICK_SCORE_INTERVAL = 20L;
    public final static Long LIKE_SCORE_INTERVAL = 5L;

    @Autowired
    private PostInteractionStatsRepository postInteractionStatsRepository;
    @Autowired
    private BucketRepository bucketRepository;

    public void processWatchTime(InteractionEventPostWatched event) {
        UUID postId = event.getPostId();
        Long watchTime = event.getWatchTime();
        this.postInteractionStatsRepository.incrementTempTotalWatchTime(postId,watchTime);//haydi tene chi

        Optional<PostInteractionStats> postInteractionStatsOpt = this.postInteractionStatsRepository.findByPostId(postId);
        if(postInteractionStatsOpt.isEmpty())
        {
            log.info("Stop process post is not boosted");
            return;
        }

        PostInteractionStats postInteractionStats = postInteractionStatsOpt.get();
        Long totalWatchTime = postInteractionStats.getTempTotalWatchTime();
        Long scoreUpdateCount= postInteractionStats.getScoreUpdateCount();

        if(totalWatchTime % (WATCH_SCORE_INTERVAL * scoreUpdateCount) != totalWatchTime){
            updateScoring(postInteractionStats,postId);
        }
    }



    public void processClick(InteractionEventPostClicked event){
        UUID postId = event.getPostId();

        this.postInteractionStatsRepository.incrementTempTotalClick(postId);

        Optional<PostInteractionStats> postInteractionStatsOpt = this.postInteractionStatsRepository.findByPostId(postId);
        if(postInteractionStatsOpt.isEmpty())
        {
            log.info("Stop process post is not boosted");
            return;
        }

        PostInteractionStats postInteractionStats = postInteractionStatsOpt.get();
        Long totalTempClick = postInteractionStats.getTempTotalClick();
        Long scoreUpdateCount= postInteractionStats.getScoreUpdateCount();

        if(totalTempClick % (CLICK_SCORE_INTERVAL * scoreUpdateCount) != totalTempClick){
            updateScoring(postInteractionStats,postId);
        }
    }


    public void processLike(InteractionEventToggleLike event){
        UUID postId = event.getPostId();

        switch (event.getToggleLikeAction())
        {
            case INSERT -> {
                this.postInteractionStatsRepository.incrementTempTotalLike(postId);
                Optional<PostInteractionStats> postInteractionStatsOpt = this.postInteractionStatsRepository.findByPostId(postId);
                if(postInteractionStatsOpt.isEmpty())
                {
                    log.info("Stop process post is not boosted");
                    return;
                }
                PostInteractionStats postInteractionStats = postInteractionStatsOpt.get();
                Long totalTempLike = postInteractionStats.getTempTotalLike();
                Long scoreUpdateCount= postInteractionStats.getScoreUpdateCount();

                if(totalTempLike % (LIKE_SCORE_INTERVAL * scoreUpdateCount) != totalTempLike)
                    updateScoring(postInteractionStats,postId);

            }
            case UPDATE_LIKED -> this.postInteractionStatsRepository.incrementTempTotalLike(postId);

            case UPDATE_UNLIKED -> this.postInteractionStatsRepository.decrementTempTotalLike(postId);

        }

    }

    private void updateScoring(PostInteractionStats postInteractionStats, UUID postId)
    {
        Long tempTotalLike = postInteractionStats.getTempTotalLike();
        Long tempTotalUpvote = postInteractionStats.getTempTotalUpvote();
        Long tempTotalClick = postInteractionStats.getTempTotalClick();
        Long tempTotalWatchTime = postInteractionStats.getTempTotalWatchTime();
        Long boostedAt = postInteractionStats.getBoostedAt();
        PostScoreBucket postScoreBucket = new PostScoreBucket(postId,tempTotalLike,tempTotalUpvote,tempTotalClick,tempTotalWatchTime,boostedAt);

        this.bucketRepository.updatePostScore(postScoreBucket);
        this.postInteractionStatsRepository.incrementScoreUpdateCount(postId);

    }


    public void processUpvote(InteractionEventToggleUpvote event){
        switch (event.getToggleUpvoteState())
        {
            case INSERT -> {
                PostInteractionStats postInteractionStats  = fromInteractionEventToggleUpvote(event);
                PostScoreBucket postScoreBucket =
                        new PostScoreBucket(
                                postInteractionStats.getPostId(),postInteractionStats.getTempTotalLike(),postInteractionStats.getTempTotalUpvote(),postInteractionStats.getTempTotalClick(), postInteractionStats.getTempTotalWatchTime(),event.getBoostedAt()
                        );
                this.bucketRepository.addPostScore(postScoreBucket);
                this.postInteractionStatsRepository.save(postInteractionStats);
            }
//            if not exist in those case ignore
            case UPDATE_ADDED_UPVOTE -> this.postInteractionStatsRepository.incrementTempTotalUpvote(event.getPostId());
            case UPDATE_REMOVED_UPVOTE -> this.postInteractionStatsRepository.decrementTempTotalUpvote(event.getPostId());
        }
    }

    private   PostInteractionStats fromInteractionEventToggleUpvote(InteractionEventToggleUpvote event) {
        PostInteractionStats stats = PostInteractionStats.builder()
                .postId(event.getPostId())
                .boostedAt(event.getBoostedAt())
                .build();

        // Only set totals if the event is an INSERT
        if (event.getToggleUpvoteState() == ToggleUpvoteState.INSERT) {
            stats.setTempTotalLike(event.getTotalLike());
            stats.setTempTotalUpvote(event.getTotalUpvote());
            stats.setTempTotalClick(event.getTotalClick());
            stats.setTempTotalWatchTime(event.getTotalWatchTime());
        }

        // scoreUpdateCount starts at 1 by default; you can increment later if needed
        return stats;
    }

}
