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

    @Column(name = "content_type", nullable = false, length = 50)
    private String contentType;

    @Column(name = "video_uri", nullable = false, columnDefinition = "TEXT")
    private String videoUri;

    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible = true;

    @Column(nullable = false, length = 50)
    private String status = "pending";

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime updatedAt = OffsetDateTime.now();


    public static boolean postExist(Optional<Post> postOptional)
    {
        if(postOptional.isEmpty())
            return false;
        return true;
    }
}

