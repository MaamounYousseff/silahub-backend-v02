package com.example.post.api;

import java.util.UUID;

public interface PostInteractionPort
{
    PostInteractionCreatedDto postCreated(UUID postId);
}