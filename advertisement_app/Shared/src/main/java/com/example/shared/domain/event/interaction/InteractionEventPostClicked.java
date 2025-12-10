package com.example.shared.domain.event.interaction;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;


@Data
public class InteractionEventPostClicked {
    private UUID postId;
    private OffsetDateTime timeStamp;

    // Manual constructor that sets timestamp automatically
    public InteractionEventPostClicked(UUID postId) {
        this.postId = postId;
        this.timeStamp = OffsetDateTime.now();
    }
}