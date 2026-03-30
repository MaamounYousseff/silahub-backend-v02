package com.example.shared.interaction;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class InteractionEventToggleLike {
    private UUID postId;
    private UUID explorerId;
    private ToggleLikeAction toggleLikeAction;
    private OffsetDateTime timeStamp;

    // Manual constructor that sets timestamp automatically
    public InteractionEventToggleLike(UUID postId, UUID explorerId, ToggleLikeAction toggleLikeAction) {
        this.postId = postId;
        this.explorerId = explorerId;
        this.toggleLikeAction = toggleLikeAction;
        this.timeStamp = OffsetDateTime.now();
    }
}
