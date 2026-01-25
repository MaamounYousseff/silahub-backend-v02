package com.example.post.web;

import com.example.post.domain.Post;
import java.util.UUID;

public class PostMapper
{
    public static final String DELIMITER = "_";
    public static String S3_DELIMINETER = "/";
    public static String RAW_PATH = "posts/raw/<object_s3_name>";

    public static Post fromPostIntentCreateRequest(PostIntentCreateRequest request, UUID creatorId) {
        Post post = new Post();

        post.setCreatorId(creatorId);
        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setIsVisible(request.getIsVisible());
        post.setStatus("pending");
        post.setImageCount((short) request.getImageCount());
        post.setThumbnailCount((short) request.getThumbnailCount());

        return post;
    }
}
