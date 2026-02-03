package com.example.feed.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private List<String> imageUrls;
    private String whatsappNumber;
    private Float longitude;
    private Float latitude;

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

    @Builder.Default
    private List<UUID> likeBy = new ArrayList<>();

    @Builder.Default
    private List<UUID> upvotedBy = new ArrayList<>();

    private Long boostedAt;

    private String status;


    public static boolean exist(Optional<FeedPost> feedPostOptional)
    {
        if(feedPostOptional.isEmpty())
            return false;
        return true;
    }
}
