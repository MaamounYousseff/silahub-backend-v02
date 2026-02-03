package com.example.feed.api;

import com.example.feed.logic.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FeedAssetUploadedPortImpl implements FeedAssetUploadedPort
{
    @Autowired
    private FeedService feedService;

    @Override
    public void assetUploaded(UUID postId, String assetUrl, String assetType) {
        this.feedService.addNewAsset(postId, assetUrl, assetType);
    }

}
