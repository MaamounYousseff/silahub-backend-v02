package com.example.interaction.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "post_likes")
public class PostLike {

    @Id
    private UUID id;

    private UUID explorerId;

    private OffsetDateTime likedAt;

    private boolean liked;
}
