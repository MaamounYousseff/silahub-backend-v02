package com.example.post.logic;

import com.example.post.api.PostInteractionCreatedDto;
import com.example.post.api.PostInteractionPort;
import com.example.post.domain.*;
import com.example.post.web.PostIntentCreateRequest;
import com.example.post.web.PostMapper;
import com.example.shared.post.EventPostCreated;
import com.example.shared.security.CurrentUserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

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
        retrievalPost.setObjectS3KeySuffix(postIn.getObjectS3KeySuffix());
        retrievalPost.setS3VideoUri(postIn.getS3VideoUri());

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
        EventPostCreated eventPostCreated = EventPostCreated.builder()
                .postId(retrievalPost.getId())
                .videoUrl(retrievalPost.getS3VideoUri())
                .creatorId(retrievalPost.getCreatorId())
                .timeStamp(OffsetDateTime.now().toEpochSecond())
                .boostedAt(postInteractionCreatedDto.getBoostedAt())
                .build();

        this.publisher.publishEvent(eventPostCreated);
    }

    @Transactional
    public PreSignUrlsResult createPostIntent(PostIntentCreateRequest postIntentCreateRequest) {

        Post post = fromPostIntentCreateRequest(postIntentCreateRequest, currentUserContext.getUserId());

        // ================= VIDEO =================
        String videoObjectName = UUID.randomUUID() + DELIMITER + "v";
        String videoS3Prefix = PostMapper.RAW_PATH.replace("<object_s3_name>", videoObjectName);
        String videoPreSignUrl = generatePutPresignedUrl(videoS3Prefix, BUCKET_NAME, s3Presigner, ".mp4");

        // ================= SAVE POST INTENT INFO=================
        post.setObjectS3KeyPrefix(videoS3Prefix);
        postRepository.save(post);

        // ================= IMAGES =================
        List<AssetPreSignUrlResult> imagePreSignUrlResults = new ArrayList<>();
        for (int i = 0; i < postIntentCreateRequest.getImageContentTypes().size(); i++) {
            String imageObjectName = UUID.randomUUID().toString();
            String imageS3Prefix = PostMapper.ASSETS_IMAGE_PATH.replace("<object_s3_name>", imageObjectName);
            String contentType = getS3Suffix(postIntentCreateRequest.getImageContentTypes().get(i));
            String imagePreSignUrl = generatePutPresignedUrl(imageS3Prefix, BUCKET_NAME, s3Presigner, contentType);

            AssetPreSignUrlResult assetImagePreSignUrlResult  = new AssetPreSignUrlResult();
            assetImagePreSignUrlResult.setUrl(imagePreSignUrl);
            assetImagePreSignUrlResult.setContentType(contentType);

            imagePreSignUrlResults.add(assetImagePreSignUrlResult);

            PostAsset postAsset = new PostAsset();
            postAsset.setS3AssetPrefix(imageS3Prefix);
            postAsset.setStatus("pending");
            postAsset.setPostId(post.getId());
            postAsset.setType("image");
            postAssetRepo.save(postAsset);
        }

        // ================= THUMBNAIL =================
        AssetPreSignUrlResult thumbnailPreSignUrlResult = new AssetPreSignUrlResult();

        if (post.getThumbnailCount() > 0) {
            String thumbnailObjectName = UUID.randomUUID().toString();
            String thumbnailS3Prefix = PostMapper.ASSETS_THUMBNAIL_PATH.replace("<object_s3_name>", thumbnailObjectName);
            String contentType = getS3Suffix(postIntentCreateRequest.getThumbnailContentType());
            String thumbnailPreSignUrl = generatePutPresignedUrl(thumbnailS3Prefix, BUCKET_NAME, s3Presigner,contentType);
            thumbnailPreSignUrlResult.setContentType(contentType);
            thumbnailPreSignUrlResult.setUrl(thumbnailPreSignUrl);
            PostAsset postAsset = new PostAsset();
            postAsset.setS3AssetPrefix(thumbnailS3Prefix);
            postAsset.setStatus("pending");
            postAsset.setPostId(post.getId());
            postAsset.setType("thumbnail");
            postAssetRepo.save(postAsset);
        }



        // ================= RESULT =================
        PreSignUrlsResult result = new PreSignUrlsResult();
        result.setVideoPreSignUrl(videoPreSignUrl);
        result.setImagePreSignUrls(imagePreSignUrlResults);
        result.setThumbnailPreSignUrl(thumbnailPreSignUrlResult);

        return result;
    }
    private String getS3Suffix(String contentType) {
        switch (contentType) {
            case "image/jpeg":
            case "thumbnail/jpeg":
                return ".jpg";

            case "image/png":
            case "thumbnail/png":
                return ".png";

            default:
                throw new IllegalArgumentException("Unsupported content type: " + contentType);
        }
    }


}
