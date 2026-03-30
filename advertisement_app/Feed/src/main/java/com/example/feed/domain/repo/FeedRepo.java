package com.example.feed.domain.repo;

import com.example.feed.domain.model.FeedPost;

import java.util.Optional;
import java.util.UUID;

public interface FeedRepo
{
    FeedPost save(FeedPost feedPost);
    Optional<FeedPost> findByPostId(UUID postId);
    boolean removeLike(UUID postId, UUID explorerId);
    boolean addLike(UUID postId, UUID explorerId);
    boolean addUpvote(UUID postId, UUID promoterId, Long boostAt);
    boolean  updateUpvote(UUID postId, UUID promoterId);
    boolean removeUpvote(UUID postId, UUID promoterId);
    boolean addNewAsset(UUID postId, String assetUrl,String type);
    void update(FeedPost feed);
}
