package com.example.post.domain;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Builder
@Entity
@Table(name = "post_assets")
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


    @Column(name = "s3_asset_prefix",nullable = false)
    private String s3AssetPrefix;

    @Column(name = "s3_asset_suffix")
    private String s3AssetSuffix;

    @Column(name = "s3_asset_uri")
    private String s3Uri;

    @Builder.Default
    @Column(nullable = false)
    private String status = "pending";
}

