package com.example.useradmin.api;

import java.util.UUID;

public interface FeedCreatorPort
{
    FeedCreatorDto getCreatorProfile(UUID creatorId);
}
