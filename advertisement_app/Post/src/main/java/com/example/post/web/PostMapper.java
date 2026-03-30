package com.example.post.web;

import com.example.post.domain.Post;
import java.util.UUID;

public class PostMapper
{
    public static final String DELIMITER = "_";
    public static String S3_DELIMINETER = "/";
    public static String RAW_PATH = "posts/raw/<object_s3_name>";
    public static final String ASSETS_IMAGE_PATH = "posts/assets/origin/images/<object_s3_name>";
    public static final String ASSETS_THUMBNAIL_PATH = "posts/assets/origin/thumbnails/<object_s3_name>";

    public static Post fromPostIntentCreateRequest(PostIntentCreateRequest request, UUID creatorId) {
        Post post = new Post();

        post.setCreatorId(creatorId);
        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setIsVisible(request.getIsVisible());
        post.setStatus("pending");
        post.setImageCount((short) request.getImageContentTypes().size());
        post.setThumbnailCount(request.getThumbnailContentType() != null ? (short)1 : (short)0);
        return post;
    }
}
