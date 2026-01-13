package com.example.post.web;

import com.example.post.domain.Post;
import com.example.post.domain.PostStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public class PostMapper
{

    public static Post fromPostCreateRequest(PostCreateRequest request, UUID creatorId) {
        Post post = new Post();

        post.setCreatorId(creatorId);
        post.setThumbnailUrl(request.getThumbnailUrl());
        post.setVideoUrl(request.getVideoUrl());
        post.setImageUrls(request.getImageUrls());
        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setContentType(request.getContentType());
        post.setIsVisible(request.getIsVisible());

        // Defaults for creation
        post.setIsActive(true);
        OffsetDateTime now = OffsetDateTime.now();
        post.setCreatedAt(now);
        post.setUpdatedAt(now);
        post.setStatus(PostStatus.DRAFT.name().toLowerCase());

        return post;
    }
}
