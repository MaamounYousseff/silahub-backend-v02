package com.example.post.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostAssetRepo extends JpaRepository<PostAsset, UUID>
{
    List<PostAsset> findByPostId(UUID postId);
}
