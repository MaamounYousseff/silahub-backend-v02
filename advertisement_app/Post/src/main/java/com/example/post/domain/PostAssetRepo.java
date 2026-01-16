package com.example.post.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostAssetRepo extends JpaRepository<PostAsset, UUID>
{
    Optional<PostAsset> findByS3AssetPrefix(String s3AssetPrefix);
    List<PostAsset> findByPostId(UUID postId);


    @Modifying
    @Transactional
    @Query("""
        UPDATE PostAsset p
        SET p.s3AssetUri = :assetUri,
            p.s3AssetSuffix = :s3AssetSuffix,
            p.status = :status
        WHERE p.id = :postAssetId
    """)
    int update(UUID postAssetId , String assetUri ,String s3AssetSuffix , String status);
}
