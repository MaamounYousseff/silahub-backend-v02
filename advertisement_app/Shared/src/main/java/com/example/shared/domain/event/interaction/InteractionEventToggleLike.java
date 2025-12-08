package com.example.shared.domain.event.interaction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class InteractionEventToggleLike
{
    private UUID feedPostId;
    private UUID explorerId;
    private ToggleLikeAction toggleLikeAction;

    private OffsetDateTime timeStamp;


}
