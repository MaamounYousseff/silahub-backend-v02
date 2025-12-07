package com.example.shared.domain.event.interaction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class InteractionEventUpvote
{
    private UUID feedPostId;
    private UUID promoterId;
    private Long timeStamp;
}
