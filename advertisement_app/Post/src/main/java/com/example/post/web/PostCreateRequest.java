package com.example.post.web;

import java.util.List;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateRequest {

    @NotBlank(message = "thumbnailUrl is required")
    @Size(max = 500, message = "thumbnailUrl must be at most 500 characters")
    @Pattern(
            regexp = "^(http|https)://.*$",
            message = "thumbnailUrl must be a valid URL"
    )
    private String thumbnailUrl;

    @NotBlank(message = "videoUrl is required")
    @Size(max = 500, message = "videoUrl must be at most 500 characters")
    @Pattern(
            regexp = "^(http|https)://.*$",
            message = "videoUrl must be a valid URL"
    )
    private String videoUrl;

    @NotNull(message = "imageUrls is required")
    @Size(max = 3,  message = "Maximum 3 images are allowed")
    @Size(min = 1,  message = "minimum 1 image are allowed")
    private List<@NotBlank @Pattern(regexp = "^(http|https)://.*$", message = "Each imageUrl must be a valid URL") String> imageUrls;

    @NotBlank(message = "title is required")
    @Size(max = 255, message = "title must be at most 255 characters")
    private String title;

    @Size(max = 2000, message = "description must be at most 2000 characters")
    private String description;

    @NotBlank(message = "contentType is required")
    @Pattern(
            regexp = "^(video|image|text)$",
            message = "contentType must be one of: video, image, text"
    )
    private String contentType;

    @NotNull(message = "isVisible is required")
    private Boolean isVisible;
}
