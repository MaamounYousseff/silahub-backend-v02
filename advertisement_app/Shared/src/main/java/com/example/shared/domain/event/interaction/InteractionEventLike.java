package com.example.shared.domain.event.interaction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class InteractionEventLike
{
    private UUID feedPostId;
    private UUID explorerId;
    private Long timeStamp;
}
