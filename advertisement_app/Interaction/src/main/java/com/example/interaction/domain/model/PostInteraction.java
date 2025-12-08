package com.example.interaction.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "post_interactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostInteraction {

    @Id
    @Column(name = "post_id", nullable = false)
    private UUID postId;

    @Column(name = "total_views", nullable = false)
    private Long totalViews = 0L;

    @Column(name = "total_upvotes", nullable = false)
    private Long totalUpvotes = 0L;

    @Column(name = "total_likes", nullable = false)
    private Long totalLikes = 0L;

    @Column(name = "total_watch_seconds", nullable = false)
    private Long totalWatchSeconds = 0L;

    @Column(name = "total_clicks", nullable = false)
    private Long totalClicks = 0L;
}

