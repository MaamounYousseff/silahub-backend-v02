package com.example.interaction.api;

import java.util.UUID;

public interface PostInteractionPort
{
    PostInteractionCreatedDto postCreated(UUID postId);
}
