package com.example.scoring.logic;

import com.example.scoring.domain.model.PostInteractionStats;
import com.example.scoring.domain.model.PostScoreBucket;
import com.example.scoring.domain.repo.BucketRepository;
import com.example.scoring.domain.repo.PostInteractionStatsRepository;
import com.example.shared.domain.event.interaction.InteractionEventPostClicked;
import com.example.shared.domain.event.interaction.InteractionEventPostWatched;
import com.example.shared.domain.event.interaction.InteractionEventToggleLike;
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


    public void processUpvote(InteractionEventPostWatched event){
//        TODO PUBLISH boost_post command

//        TODO listen to post_boosted event and update the post information in the repo
    }
}
