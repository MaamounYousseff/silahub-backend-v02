package com.example.shared.domain.event.interaction;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class InteractionEventWatchTime {
    private UUID postId;
    private UUID explorerId;
    private OffsetDateTime timeStamp;

    // Manual constructor that sets timestamp automatically
    public InteractionEventWatchTime(UUID postId, UUID explorerId) {
        this.postId = postId;
        this.explorerId = explorerId;
        this.timeStamp = OffsetDateTime.now();
    }
}