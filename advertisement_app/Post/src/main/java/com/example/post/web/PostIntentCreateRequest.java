package com.example.post.web;


import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;


@Getter @Setter
public class PostIntentCreateRequest {

    @Size(max = 3, message = "Cannot upload more than 3 images")
    private List<@NotBlank(message = "Image content type cannot be blank")
    @Pattern(regexp = "^image/(jpeg|png)$",
            message = "Invalid image content type")
            String> imageContentTypes;

    @Pattern(regexp = "^image/(jpeg|png)$",
            message = "Invalid thumbnail content type")
    private String thumbnailContentType;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be at most 255 characters")
    private String title;

    @Size(max = 2000, message = "Description must be at most 2000 characters")
    private String description;

    @NotNull(message = "isVisible is required")
    private Boolean isVisible;
}