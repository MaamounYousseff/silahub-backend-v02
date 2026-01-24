package com.example.post.web;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostIntentCreateResponse
{
    private String contentType;
    private String preSignedUrl;
}
