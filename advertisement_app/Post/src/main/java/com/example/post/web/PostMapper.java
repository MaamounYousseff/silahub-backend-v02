package com.example.post.web;

import com.example.post.domain.Post;
import java.util.UUID;

public class PostMapper
{
    public static final String DELIMITER = "_";
    public static String S3_DELIMINETER = "/";
    public static String RAW_PATH = "posts/raw/<object_s3_name>";

    public static Post fromPostCreateRequest(PostCreateRequest request, UUID creatorId) {
        Post post = new Post();

        // Basic info
        post.setCreatorId(creatorId);
        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setIsVisible(request.getIsVisible());
        post.setStatus("pending");

        // Video URL
        String objectName = UUID.randomUUID() + DELIMITER + "v" ;

        String objectKey =  RAW_PATH.replace("<object_s3_name>", objectName) ;
        post.setObjectS3KeyPrefix(objectKey);

        // Counts
        post.setImageCount((short) request.getImageCount());
        post.setThumbnailCount((short) request.getThumbnailCount());

        return post;
    }
}
