package com.example.feed.domain.repo;

import com.example.feed.domain.model.FeedPost;

public interface FeedRepo
{
    FeedPost save(FeedPost feedPost);
}
