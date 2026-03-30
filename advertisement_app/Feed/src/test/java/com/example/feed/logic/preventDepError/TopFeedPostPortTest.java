package com.example.feed.logic.preventDepError;

import com.example.feed.api.TopFeedPostDto;
import com.example.feed.api.TopFeedPostPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TopFeedPostPortTest implements TopFeedPostPort
{
    @Override
    public Optional<TopFeedPostDto> getNTopPosts(int offset) {
        return Optional.empty();
    }
}
