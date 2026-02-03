package com.example.post.logic;

import com.example.post.domain.PostAsset;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class PostServiceHelper
{
    public static  String generatePutPresignedUrl(String filePath, String bucketName, S3Presigner s3Presigner, String contentTypeSuffix) {
        PutObjectRequest.Builder putObjectRequestBuilder = PutObjectRequest.builder()
                .bucket(bucketName )
                .key(filePath+ contentTypeSuffix);

        PutObjectRequest putObjectRequest = putObjectRequestBuilder.build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        return presignedRequest.url().toString();
    }



    public static Optional<List<String>> getImagesUri(List<PostAsset> postAssetList) {
        if (postAssetList == null) {
            return null;
        }
        List<String> images = postAssetList.stream()
                .filter(e -> e.getType().equalsIgnoreCase("image"))
                .map(PostAsset::getS3AssetUrl)
                .toList();

        return images.isEmpty() ? Optional.empty() : Optional.of(images);
    }


    public static String getThumbnail(List<PostAsset> postAssetList) {
        if (postAssetList == null) {
            return null;
        }
        return postAssetList.stream()
                .filter(e -> e.getType().equalsIgnoreCase("thumbnail"))
                .map(PostAsset::getS3AssetUrl)
                .findFirst()
                .orElse(null);
    }

}
