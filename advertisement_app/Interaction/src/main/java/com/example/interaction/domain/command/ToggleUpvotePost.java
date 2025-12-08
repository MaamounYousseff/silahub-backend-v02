package com.example.interaction.domain.command;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@Data
public class ToggleUpvotePost
{
    private UUID postId;
    private UUID promoterId;

    @Setter(AccessLevel.NONE)
    private OffsetDateTime timeStamp;

    public ToggleUpvotePost(UUID postId, UUID promoterId) {
        this.postId = postId;
        this.promoterId = promoterId;
        this.timeStamp = OffsetDateTime.now();
    }
}