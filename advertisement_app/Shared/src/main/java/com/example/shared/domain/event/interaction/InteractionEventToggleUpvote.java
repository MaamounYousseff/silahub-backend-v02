package com.example.shared.domain.event.interaction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class InteractionEventToggleUpvote
{
    private UUID feedPostId;
    private UUID promoterId;
    private ToggleUpvoteState toggleUpvoteState;

    private OffsetDateTime timeStamp;

}
