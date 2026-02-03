package com.example.feed.api;

import java.util.UUID;

public interface FeedAssetUploadedPort
{
    void assetUploaded(UUID postId, String assetUrl, String assetType);

}
