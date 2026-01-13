package com.example.post.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "job_video")
@Getter
@Setter
public class JobVideo {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "video_s3_key", nullable = false, columnDefinition = "TEXT")
    private String videoS3Key;

    @Column(nullable = false, length = 50)
    private String status;
}
