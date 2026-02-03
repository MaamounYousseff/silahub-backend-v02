package com.example.post.logic;

import com.example.feed.api.FeedAssetUploadedPort;
import com.example.post.domain.PostAsset;
import com.example.post.domain.PostAssetRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PostAssetService
{
    @Autowired
    private PostAssetRepo postAssetRepo;
    @Autowired
    private FeedAssetUploadedPort feedAssetUploadedPort;


    public PostAsset findByS3AssetPrefix(String s3AssetPrefix)
    {
        Optional<PostAsset> postAssetOptional = this.postAssetRepo.findByS3AssetPrefix(s3AssetPrefix);
        if (!PostAsset.exist(postAssetOptional))
            throw new RuntimeException("There was no postAsset for this prefix: " + s3AssetPrefix);
        PostAsset postAsset = postAssetOptional.get();
        return postAsset;
    }

    public void update(PostAsset postAsset)
    {
        int row = this.postAssetRepo.update(postAsset.getId() , postAsset.getS3AssetUrl() ,postAsset.getS3AssetSuffix() , "active");
        if (row == 0)
            throw new RuntimeException("Failed to update PostAsset Status to ACTIVE \n PostAsset Id: " + postAsset.getPostId());
    }
}
