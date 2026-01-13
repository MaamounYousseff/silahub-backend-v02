package com.example.post.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "post_asset")
@Getter
@Setter
public class PostAsset {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "post_id", nullable = false)
    private UUID  postId;

    @Column(nullable = false, length = 50)
    private String type; // 'thumbnail' or 'image'

    @Column(nullable = false, columnDefinition = "TEXT")
    private String uri;
}

