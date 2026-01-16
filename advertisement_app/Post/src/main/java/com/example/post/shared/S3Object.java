package com.example.post.shared;

import lombok.Data;

@Data
public class S3Object {
    private String key;
    private Long size;
    private String eTag;
    private String sequencer;
}
