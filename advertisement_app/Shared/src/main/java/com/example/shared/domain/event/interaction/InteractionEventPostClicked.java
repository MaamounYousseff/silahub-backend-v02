package com.example.shared.domain.event.interaction;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;


@Data
public class InteractionEventPostClicked {
    private UUID feedPostId;
    private OffsetDateTime timeStamp;

    // Manual constructor that sets timestamp automatically
    public InteractionEventPostClicked(UUID feedPostId) {
        this.feedPostId = feedPostId;
        this.timeStamp = OffsetDateTime.now();
    }
}