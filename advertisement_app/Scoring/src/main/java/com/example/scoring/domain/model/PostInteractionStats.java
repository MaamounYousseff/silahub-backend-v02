package com.example.scoring.domain.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private Long tempTotalLike;
    private Long tempTotalUpvote;
    private Long tempTotalClick;
    private Long tempTotalWatchTime;

    private Long scoreUpdateCount;

    private Long boostedAt;


}