package com.example.scoring.domain.model;

import lombok.Getter;
import java.util.UUID;

public class PostScoreBucket
{
    @Getter
    private UUID postId;
    @Getter
    private Long score;


    // Internal interaction attributes to calculate score internally
    private Long tempTotalLike;
    private Long tempTotalUpvote;
    private Long tempTotalClick;
    private Long tempTotalWatchTime;
    private Long boostedAt;

//    Todo .. remove the score it should be just calculated internally so we just have a getter for it
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
        long baseScore = tempTotalClick * 1L + tempTotalLike * 5L + tempTotalUpvote * 3L + (tempTotalWatchTime / 600L);

        // Optionally factor in boostedAt (e.g., decay or boost)
        double factor = 1.0;
        if (boostedAt != null && boostedAt > 0) {
            factor = 1.0 / (1.0 + (boostedAt / 3600.0 * 0.05)); // example decay per hour
        }

        this.score = Math.round(baseScore * factor);
    }
}
