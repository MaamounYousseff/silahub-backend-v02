package com.example.post.web;


import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateRequest {

    @Min(value = 0, message = "Number of images must be at least 0")
    @Max(value = 3, message = "Number of images must be at most 3")
    private int imageCount;

    @Min(value = 0, message = "Number of thumbnails must be at least 0")
    @Max(value = 1, message = "Number of thumbnails must be at most 1")
    private int thumbnailCount;

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
