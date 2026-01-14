package com.example.post.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Entity
@Table(name = "posts")
@Getter
@Setter
public class Post {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "creator_id", nullable = false, columnDefinition = "UUID")
    private UUID creatorId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "object_s3_key_prefix", nullable = false)
    private String objectS3KeyPrefix;

    @Column(name = "object_s3_key_suffix")
    private String objectS3KeySuffix;

    @Column(name = "s3_video_uri")
    private String s3VideoUri;

    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible = true;

    @Column(nullable = false, length = 50)
    private String status = "pending";

    @Column(name = "image_count", nullable = false)
    private Short imageCount;

    @Column(name = "thumbnail_count", nullable = false)
    private Short thumbnailCount;

    // Lazy-loaded list of assets for this post
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<PostAsset> assets;

    public static boolean postExist(Optional<Post> postOptional)
    {
        if(postOptional.isEmpty())
            return false;
        return true;
    }
}

