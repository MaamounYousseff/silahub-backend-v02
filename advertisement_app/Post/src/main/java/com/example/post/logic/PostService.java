package com.example.post.logic;

import com.example.post.api.PostInteractionCreatedDto;
import com.example.post.api.PostInteractionPort;
import com.example.post.domain.*;
import com.example.post.web.PostIntentCreateRequest;
import com.example.post.web.PostMapper;
import com.example.shared.post.EventPostCreated;
import com.example.shared.security.CurrentUserContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.post.Constant.*;
import static com.example.post.logic.PostServiceHelper.*;
import static com.example.post.web.PostMapper.fromPostIntentCreateRequest;

@Service
@Slf4j
public class PostService
{
    @Autowired
    private CurrentUserContext currentUserContext;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private PostInteractionPort postInteractionPort;
    @Autowired
    private PostAssetRepo postAssetRepo;
    @Autowired
    private S3Presigner s3Presigner;



    @jakarta.transaction.Transactional
    public Post createPost(PostIntentCreateRequest postRequest)
    {
        log.info("Starting creation of post for userId={}", currentUserContext.getUserId());

        Post post = fromPostIntentCreateRequest(
                postRequest,
                currentUserContext.getUserId()
        );
        log.debug("Post mapped from request: title='{}', totalImages={}, totalThumbnails={}",
                post.getTitle(), post.getImageCount(), post.getThumbnailCount());

        Post storedPost = postRepository.save(post);
        log.info("Post saved with ID={}", storedPost.getId());

        List<PostAsset> assets = new ArrayList<>();
        for (int i = 0; i < storedPost.getImageCount(); i++) {
            String imageKey = NEW_THUMBNAIL_PATH.replace("<object_s3_name>" , post.getId().toString()) + DELIMITER + IMAGE_Name_SUFFIX + DELIMITER + i;

            PostAsset imageAsset = PostAsset.builder()
                    .postId(storedPost.getId())
                    .type("image")
                    .s3AssetPrefix(imageKey)
                    .build();

            postAssetRepo.save(imageAsset);
            assets.add(imageAsset);

            log.debug("Image PostAsset created: {}");
        }

        for (int i = 0; i < storedPost.getThumbnailCount(); i++) {
            String thumbnailKey = NEW_IMAGE_PATH.replace("<object_s3_name>" , post.getId().toString()) + DELIMITER + IMAGE_Name_SUFFIX + DELIMITER + i;

            PostAsset thumbnailAsset = PostAsset.builder()
                    .postId(storedPost.getId())
                    .type("thumbnail")
                    .s3AssetPrefix(thumbnailKey)
                    .build();

            postAssetRepo.save(thumbnailAsset);
            assets.add(thumbnailAsset);

            log.debug("Thumbnail PostAsset created: {}");
        }

        storedPost.setAssets(assets);
        log.info("Total assets linked to Post ID {}: {}", storedPost.getId(), assets.size());

        log.info("Post creation completed for Post ID={}", storedPost.getId());
        return storedPost;
    }

    @Transactional
    public void postCreated(UUID postId)
    {
        Optional<Post> postOptional = this.postRepository.findById(postId);
        if(!Post.postExist(postOptional))
        {
            log.error("Post not exist");
            return;
        }

    }

    public Post findByObjectS3KeyPrefix(String objectKeyPrefix){
        Optional<Post> postOptional = this.postRepository.findByObjectS3KeyPrefix(objectKeyPrefix);
        if (!Post.postExist(postOptional))
            throw new RuntimeException("There was no post for this key : " + objectKeyPrefix);
        return postOptional.get();
    }

    public void updatePostToDraft(UUID postId, String videoUri,String  objectKeySuffix)
    {
        int row = this.postRepository.update(postId, videoUri, objectKeySuffix, "draft");
        if (row == 0)
            throw new RuntimeException("Failed To update Post Status to draft \n Post Id: " + postId);
    }

    @Transactional
    public void postUploadCompleted(Post postIn)
    {
        String objectS3Key= postIn.getObjectS3KeyPrefix();
        Optional<Post> postOptional = postRepository.findByObjectS3KeyPrefix(objectS3Key);

        boolean postExist = Post.objectExistsWithKeyPrefix(postOptional);

        if(!postExist)
        {
            log.info("No Post with that object key prefix : " + postIn.getObjectS3KeyPrefix());
            return;
        }

        Post retrievalPost = postOptional.get();
        retrievalPost.setStatus("active");
        retrievalPost.setS3VideoUri(postIn.getObjectS3KeyPrefix()+ "." + postIn.getObjectS3KeySuffix());
        retrievalPost.setObjectS3KeySuffix(postIn.getObjectS3KeySuffix());

        this.postRepository.save(retrievalPost);

//        Tell Interaction Context that post is created
//        however if interaction fail so the Post storage also should be fail
        PostInteractionCreatedDto postInteractionCreatedDto = postInteractionPort.postCreated(retrievalPost.getId());
        if(postInteractionCreatedDto == null)
        {
//            the message should be get from Interaction
            throw new RuntimeException("Interaction fail");
        }

//        Publish Event that Post is Created
        List<PostAsset> postAssetList = this.postAssetRepo.findByPostId(retrievalPost.getId());
        EventPostCreated eventPostCreated = EventPostCreated.builder()
                .postId(retrievalPost.getId())
                .thumbnailUrl(getThumbnail(postAssetList))
                .ImageUrls(getImagesUri(postAssetList).isEmpty()? null : getImagesUri(postAssetList).get())
                .videoUrl(retrievalPost.getS3VideoUri())
                .creatorId(retrievalPost.getCreatorId())
                .timeStamp(OffsetDateTime.now().toEpochSecond())
                .boostedAt(postInteractionCreatedDto.getBoostedAt())
                .build();

        this.publisher.publishEvent(eventPostCreated);
    }

    public String createPostIntent(Post post)
    {
        // Video URL
        String objectName = UUID.randomUUID() + DELIMITER + "v" ;

        String objectKeyPrefix = PostMapper.RAW_PATH.replace("<object_s3_name>", objectName) ;
        post.setObjectS3KeyPrefix(objectKeyPrefix);

        String preSignedUrl = generatePutPresignedUrl(objectKeyPrefix,BUCKET_NAME,s3Presigner);

        this.postRepository.save(post);
        return preSignedUrl;

    }



}
