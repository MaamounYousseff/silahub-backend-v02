package com.example.feed.domain.repo;

import com.example.feed.domain.model.FeedPost;

import java.util.Optional;
import java.util.UUID;

public interface FeedRepo
{
    FeedPost save(FeedPost feedPost);
    Optional<FeedPost> findByPostId(UUID postId);
}
