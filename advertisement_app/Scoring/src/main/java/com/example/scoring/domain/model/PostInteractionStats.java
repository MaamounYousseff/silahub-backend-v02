package com.example.scoring.domain.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "post_interaction_stats")
public class PostInteractionStats {

    @Id
    private UUID postId;

    @Builder.Default
    private Long tempTotalLike = 0L;

    @Builder.Default
    private Long tempTotalUpvote = 0L;

    @Builder.Default
    private Long tempTotalClick = 0L;

    @Builder.Default
    private Long tempTotalWatchTime = 0L;

    @Builder.Default
    private Long scoreUpdateCount = 1L;

    private Long boostedAt;
}
