package com.example.scoring.logic;

import com.example.scoring.domain.model.PostInteractionStats;
import com.example.scoring.domain.model.PostScoreBucket;
import com.example.scoring.domain.repo.BucketRepository;
import com.example.scoring.domain.repo.PostInteractionStatsRepository;
import com.example.shared.domain.event.interaction.InteractionEventPostWatched;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class ScoringService
{
    private final static Long WATCH_SCORE_INTERVAL = 1000L;
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
//            stop the process
            log.info("Stop process post is not boosted");
            return;
        }

        PostInteractionStats postInteractionStats = postInteractionStatsOpt.get();
        Long totalWatchTime = postInteractionStats.getTempTotalWatchTime();
        Long scoreUpdateCount= postInteractionStats.getScoreUpdateCount();

        if(totalWatchTime % (WATCH_SCORE_INTERVAL * scoreUpdateCount) > 0){

            Long tempTotalLike = postInteractionStats.getTempTotalLike();
            Long tempTotalUpvote = postInteractionStats.getTempTotalUpvote();
            Long tempTotalClick = postInteractionStats.getTempTotalClick();
            Long tempTotalWatchTime = postInteractionStats.getTempTotalWatchTime();
            Long boostedAt = postInteractionStats.getBoostedAt();
            PostScoreBucket postScoreBucket = new PostScoreBucket(postId,tempTotalLike,tempTotalUpvote,tempTotalClick,tempTotalWatchTime,boostedAt);
            this.bucketRepository.updatePostScore(postScoreBucket);

            this.postInteractionStatsRepository.incrementScoreUpdateCount(postId);
        }
    }
}
