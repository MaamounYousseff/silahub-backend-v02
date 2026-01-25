package com.example.post.web.preventDepError;

import com.example.post.api.PostInteractionCreatedDto;
import com.example.post.api.PostInteractionPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class PostInteractionPortImpl implements PostInteractionPort
{
    @Override
    public PostInteractionCreatedDto postCreated(UUID postId) {
        PostInteractionCreatedDto postInteractionCreatedDto = new PostInteractionCreatedDto();
        postInteractionCreatedDto.setBoostedAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        return postInteractionCreatedDto;
    }
}
