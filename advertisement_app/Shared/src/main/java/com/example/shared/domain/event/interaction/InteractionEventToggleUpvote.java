package com.example.shared.domain.event.interaction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class InteractionEventToggleUpvote {
    private UUID feedPostId;
    private UUID promoterId;
    private ToggleUpvoteState toggleUpvoteState;
    private OffsetDateTime timeStamp;

    // Manual constructor that sets timestamp automatically
    public InteractionEventToggleUpvote(UUID feedPostId, UUID promoterId, ToggleUpvoteState toggleUpvoteState) {
        this.feedPostId = feedPostId;
        this.promoterId = promoterId;
        this.toggleUpvoteState = toggleUpvoteState;
        this.timeStamp = OffsetDateTime.now();
    }
}