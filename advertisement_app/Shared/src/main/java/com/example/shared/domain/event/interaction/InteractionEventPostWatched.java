package com.example.shared.domain.event.interaction;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class InteractionEventPostWatched {
    private UUID postId;
    private Long watchTime;
    private OffsetDateTime timeStamp;

    // Manual constructor that sets timestamp automatically
    public InteractionEventPostWatched(UUID postId, Long watchTime) {
        this.postId = postId;
        this.watchTime = watchTime;
        this.timeStamp = OffsetDateTime.now();
    }
}