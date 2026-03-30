package com.example.post.logic;

import lombok.Data;

import java.util.List;

@Data
public class PreSignUrlsResult
{
    private String videoPreSignUrl;
    private List<AssetPreSignUrlResult> imagePreSignUrls;
    private AssetPreSignUrlResult thumbnailPreSignUrl;
}
