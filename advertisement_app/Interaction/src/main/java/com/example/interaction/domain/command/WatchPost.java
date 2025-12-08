package com.example.interaction.domain.command;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@Data
public class WatchPost
{
    private UUID postId;
    private Long watchTime;

    @Setter(AccessLevel.NONE)
    private OffsetDateTime timeStamp;

    public WatchPost(UUID postId, Long watchTime) {
        this.postId = postId;
        this.watchTime = watchTime;
        this.timeStamp = OffsetDateTime.now();
    }
}
