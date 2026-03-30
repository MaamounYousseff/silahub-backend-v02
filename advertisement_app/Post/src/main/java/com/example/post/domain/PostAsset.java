package com.example.post.domain;


import jakarta.persistence.*;
import lombok.*;

import java.util.Optional;
import java.util.UUID;

@Builder
@Entity
@Table(name = "post_assets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "s3_asset_url")
    private String s3AssetUrl;

    @Builder.Default
    @Column(nullable = false)
    private String status = "pending";


    public static boolean exist(Optional<PostAsset> postAssetOptional)
    {
        if(postAssetOptional.isEmpty())
            return false;
        return true;
    }


}

