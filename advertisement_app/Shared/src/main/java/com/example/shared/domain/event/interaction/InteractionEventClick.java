package com.example.shared.domain.event.interaction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class InteractionEventClick
{
    private UUID feedPostId;
    private Long timeStamp;
}
