package com.example.interaction.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "post_upvotes")
public class PostUpvote {

    @Id
    private UUID id;

    private UUID promoterId;

    private OffsetDateTime upvotedAt;

    private boolean upvoted;
}
