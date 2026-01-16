package com.example.post.shared;

import lombok.Data;

@Data
public  class S3Bucket {
    private String name;
    private OwnerIdentity ownerIdentity;
    private String arn;
}