package com.example.shared.domain.event.interaction;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class InteractionEventToggleLike {
    private UUID feedPostId;
    private UUID explorerId;
    private ToggleLikeAction toggleLikeAction;
    private OffsetDateTime timeStamp;

    // Manual constructor that sets timestamp automatically
    public InteractionEventToggleLike(UUID feedPostId, UUID explorerId, ToggleLikeAction toggleLikeAction) {
        this.feedPostId = feedPostId;
        this.explorerId = explorerId;
        this.toggleLikeAction = toggleLikeAction;
        this.timeStamp = OffsetDateTime.now();
    }
}
