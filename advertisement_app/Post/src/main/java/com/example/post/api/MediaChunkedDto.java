package com.example.post.api;


import lombok.Data;

@Data
public class MediaChunkedDto
{
    private String objectKeyPrefix;
    private String objectKeySuffix;
    private String masterHlsIndexUri;
}
