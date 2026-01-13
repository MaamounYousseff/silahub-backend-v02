package com.example.feed.api;

import java.util.Optional;

public interface TopFeedPostPort
{
    Optional<TopFeedPostDto> getNTopPosts(int offset);
}
