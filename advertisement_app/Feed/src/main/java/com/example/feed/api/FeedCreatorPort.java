package com.example.feed.api;

import java.util.Optional;
import java.util.UUID;

public interface FeedCreatorPort
{
    Optional<FeedCreatorDto> getCreatorProfile(UUID userId);
}
