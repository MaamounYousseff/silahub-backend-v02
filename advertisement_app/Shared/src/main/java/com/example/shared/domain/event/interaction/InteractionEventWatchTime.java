package com.example.shared.domain.event.interaction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class InteractionEventWatchTime
{
    private UUID postId;
    private UUID explorerId;
}
