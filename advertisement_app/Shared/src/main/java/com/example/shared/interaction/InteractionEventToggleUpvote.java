package com.example.shared.interaction;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class InteractionEventToggleUpvote {
    private UUID postId;
    private UUID promoterId;
    private ToggleUpvoteState toggleUpvoteState;
    private OffsetDateTime timeStamp;

//    just set in the case of toggleUpvoteState >> 'INSERT'
    private Long totalLike;
    private Long totalUpvote;
    private Long totalClick;
    private Long totalWatchTime;
    private Long boostedAt;

    // Manual constructor that sets timestamp automatically
    public InteractionEventToggleUpvote(UUID postId, UUID creatorId, ToggleUpvoteState toggleUpvoteState) {
        this.postId = postId;
        this.promoterId = creatorId;
        this.toggleUpvoteState = toggleUpvoteState;
        this.timeStamp = OffsetDateTime.now();
    }

//    in case of insert
    public InteractionEventToggleUpvote(UUID postId, UUID promoterId, Long totalLike, Long totalUpvote, Long totalClick, Long totalWatchTime, Long boostedAt) {
        this.toggleUpvoteState = ToggleUpvoteState.INSERT;
        this.postId = postId;
        this.promoterId = promoterId;
        this.totalLike= totalLike;
        this.totalUpvote = totalUpvote;
        this.totalClick = totalClick;
        this.totalWatchTime = totalWatchTime;
        this.boostedAt = boostedAt;
        this.timeStamp = OffsetDateTime.now();
    }



}