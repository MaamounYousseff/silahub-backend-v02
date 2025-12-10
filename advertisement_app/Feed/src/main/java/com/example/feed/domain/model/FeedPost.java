package com.example.feed.domain.model;

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
@Document(collection = "feeds")
public class FeedPost
{
    @Id
    private UUID postId;
    private UUID creatorId;
    private Long timeStamp;
    private String videoUrl;
    private String creatorLogoUrl;
    private String creatorName;
    private String thumbnailUrl;
    private String ImageUrls;
    private String whatsapNumber;
    private float lontitude;
    private float latitude;

    @Builder.Default
    private Long tempTotalLike = 0L;

    @Builder.Default
    private Long tempTotalUpvote = 0L;

    @Builder.Default
    private Long tempTotalClick = 0L;

    @Builder.Default
    private Long tempTotalWatchTime = 0L;

    @Builder.Default
    private Long scoreUpdateCount = 0L;

    private Long boostedAt;
}
