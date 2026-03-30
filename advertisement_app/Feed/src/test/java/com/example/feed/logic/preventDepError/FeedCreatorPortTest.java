package com.example.feed.logic.preventDepError;

import com.example.feed.api.FeedCreatorDto;
import com.example.feed.api.FeedCreatorPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class FeedCreatorPortTest implements FeedCreatorPort {
    @Override
    public Optional<FeedCreatorDto> getCreatorProfile(UUID userId) {
        return Optional.empty();
    }
}
