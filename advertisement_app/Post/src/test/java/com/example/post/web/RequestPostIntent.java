package com.example.post.web;

import com.example.post.domain.Post;
import com.example.post.domain.PostRepository;
import com.example.post.logic.PostService;
import com.example.shared.security.CurrentUserContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Commit;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

import static com.example.post.web.PostMapper.fromPostIntentCreateRequest;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {
        com.example.post.PostConfig.class,
        com.example.shared.SharedConfig.class,
        com.example.shared_module_test.TestUserContext.class

})
@ComponentScan("com.example.post")
@Slf4j
public class RequestPostIntent {
    @Autowired
    private PostService postService;
    @Autowired
    private S3Presigner s3Presigner;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CurrentUserContext currentUserContext;

    @Test
    void generatePreSignedUrl() {
//        Given
        UUID uuid = UUID.randomUUID();
        String objectKeyPrefix = uuid + "_v";
        String bucketName = "amzn-s3-bucket-lb-01";
        //access Type

//        When
        String preSignedUrl = generatePutPresignedUrl(objectKeyPrefix, bucketName);

//        Then
        assertNotNull(preSignedUrl);
        log.info("preSignedUrl " + preSignedUrl);
    }


    @Test
    @Commit
    void saveIntentToDb()
    {
//      Given
        PostIntentCreateRequest postIntentCreateRequest = new PostIntentCreateRequest();
        postIntentCreateRequest.setTitle("Test Title");
        postIntentCreateRequest.setDescription("Test Description");
        postIntentCreateRequest.setIsVisible(true);
        postIntentCreateRequest.setImageCount(0);
        postIntentCreateRequest.setThumbnailCount(0);

//      When
        Post post = fromPostIntentCreateRequest(postIntentCreateRequest, currentUserContext.getUserId());
        this.postRepository.save(post);

//      Then
        assertNotNull(post);

    }

    private String generatePutPresignedUrl(String filePath,String bucketName) {
        PutObjectRequest.Builder putObjectRequestBuilder = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath);

        putObjectRequestBuilder.acl(ObjectCannedACL.PUBLIC_READ);

        PutObjectRequest putObjectRequest = putObjectRequestBuilder.build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        return presignedRequest.url().toString();
    }

}
