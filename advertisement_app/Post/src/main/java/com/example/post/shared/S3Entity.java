package com.example.post.shared;

import lombok.Data;

@Data
public class S3Entity {
    private String s3SchemaVersion;
    private String configurationId;
    private S3Bucket bucket;
    private S3Object object;
}