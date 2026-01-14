package com.example.post.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "job_videos")
@Getter
@Setter
@Builder
public class JobVideo {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "video_s3_key", nullable = false, columnDefinition = "TEXT")
    private String videoS3Key;

    @Column(name = "creator_id", nullable = false)
    private UUID creatorId;


    @Builder.Default
    @Column(nullable = false, length = 50)
    private String status = "pending";
}
