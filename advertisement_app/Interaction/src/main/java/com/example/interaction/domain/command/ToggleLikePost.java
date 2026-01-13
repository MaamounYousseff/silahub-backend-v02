package com.example.interaction.domain.command;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@Data
public class ToggleLikePost
{
    private UUID postId;
    private UUID explorerId;

    @Setter(AccessLevel.NONE)
    private OffsetDateTime timeStamp;

    public ToggleLikePost(UUID postId, UUID explorerId) {
        this.postId = postId;
        this.explorerId = explorerId;
        this.timeStamp = OffsetDateTime.now();
    }
}