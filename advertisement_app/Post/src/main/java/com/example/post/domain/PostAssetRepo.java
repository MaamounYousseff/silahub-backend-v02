package com.example.post.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostAssetRepo extends JpaRepository<PostAsset, UUID>
{
    Optional<PostAsset> findByS3AssetPrefix(String s3AssetPrefix);
    @Query("SELECT p FROM PostAsset p WHERE p.postId = :postId AND p.status = 'active'")
    List<PostAsset> findActiveByPostId(@Param("postId") UUID postId);


    @Modifying
    @Transactional
    @Query("""
        UPDATE PostAsset p
        SET p.s3AssetUrl = :assetUrl,
            p.s3AssetSuffix = :s3AssetSuffix,
            p.status = :status
        WHERE p.id = :postAssetId
    """)
    int update(UUID postAssetId , String assetUrl ,String s3AssetSuffix , String status);
}
