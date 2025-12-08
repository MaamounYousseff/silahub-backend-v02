package com.example.interaction.domain.command;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@Data
public class ClickPost
{
    private UUID postId;

    @Setter(AccessLevel.NONE)
    private OffsetDateTime timeStamp;

    public ClickPost(UUID postId) {
        this.postId = postId;
        this.timeStamp = OffsetDateTime.now();
    }
}
