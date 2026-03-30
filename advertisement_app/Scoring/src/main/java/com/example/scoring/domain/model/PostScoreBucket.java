package com.example.scoring.domain.model;

import lombok.Getter;
import java.util.UUID;

import static com.example.scoring.domain.Constant.NEW_POST_SCORE;

public class PostScoreBucket
{
    @Getter
    private UUID postId;
    @Getter
    private double score;


    // Internal interaction attributes to calculate score internally
    private Long tempTotalLike;
    private Long tempTotalUpvote;
    private Long tempTotalClick;
    private Long tempTotalWatchTime;
    private Long boostedAt;

//    Todo .. remove the score it should be just calculated internally so we just have a getter for it
    public PostScoreBucket(UUID postId)
    {
        this.postId = postId;
        this.score = NEW_POST_SCORE;
    }

    public PostScoreBucket(UUID postId, Long score)
    {
        this.postId = postId;
        this.score = score;
    }

    public PostScoreBucket (UUID postId, Long tempTotalLike, Long tempTotalUpvote, Long tempTotalClick, Long tempTotalWatchTime, Long boostedAt) {
        this.postId = postId;
        setInteractionStats(tempTotalLike,tempTotalUpvote,tempTotalClick,tempTotalWatchTime,boostedAt);
    }


    // Setter-only for interaction attributes (no getters) to recalc score
    private void setInteractionStats(Long tempTotalLike, Long tempTotalUpvote, Long tempTotalClick, Long tempTotalWatchTime, Long boostedAt) {
        this.tempTotalLike = tempTotalLike;
        this.tempTotalUpvote = tempTotalUpvote;
        this.tempTotalClick = tempTotalClick;
        this.tempTotalWatchTime = tempTotalWatchTime;
        this.boostedAt = boostedAt;
        recalculateScore();
    }

    private void recalculateScore() {
        long baseScore = tempTotalClick * 1L + tempTotalLike * 5L + tempTotalUpvote * 40L + (tempTotalWatchTime / 600L);
        double factor = 1;
        if (boostedAt != null && boostedAt > 0) {
            long nowSeconds = System.currentTimeMillis() / 1000;
            double hoursSinceBoost =
                    (nowSeconds - boostedAt) / 3600000.0;

            factor = 1.0 / (1.0 + hoursSinceBoost * 0.05);

        }

        this.score = Math.round(baseScore * factor);
    }
}
