package com.example.post.web;

import com.example.post.api.PostInteractionCreatedDto;
import com.example.post.api.PostInteractionPort;
import com.example.post.domain.Post;
import com.example.post.domain.PostAsset;
import com.example.post.domain.PostAssetRepo;
import com.example.post.domain.PostRepository;
import com.example.post.logic.PostService;
import com.example.shared.post.EventPostCreated;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.post.logic.PostServiceHelper.getImagesUri;
import static com.example.post.logic.PostServiceHelper.getThumbnail;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;


@SpringBootTest(classes = {
        com.example.post.PostConfig.class,
        com.example.shared.SharedConfig.class,
        com.example.shared_module_test.TestUserContext.class
})
@ComponentScan("com.example.post")
@Slf4j
public class PostUploadCompletedTest
{

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostAssetRepo postAssetRepo;
    @Autowired
    private PostInteractionPort postInteractionPort;
    @Mock
    private ApplicationEventPublisher publisher;

    private static Post post;

    @BeforeAll
    public static void setUp()
    {
        post = new Post();
        post.setId(UUID.fromString("6e6ecd81-3ad8-42bd-bedd-9436ca7e09c0"));
        post.setCreatorId(UUID.fromString("cfd8a81c-7bae-4f3b-a8f2-f1e280e51b43"));
        post.setTitle("My First Post");
        post.setDescription("This is a description for my first post. It can be up to 2000 characters long.");

        post.setImageCount((short) 0);
        post.setThumbnailCount((short) 0);

        post.setIsVisible(true);
        post.setStatus("pending");

        post.setObjectS3KeyPrefix("posts/raw/f40b62c3-07a0-4d07-9277-60ab4672d85b_v");
        post.setObjectS3KeySuffix(null);
        post.setS3VideoUri(null);

    }


//    pre : video is already chunked to s3
    @Test
    public void shouldFindPostByObjectS3KeyPrefix() {
        // Given
        String objectS3KeyPrefix = post.getObjectS3KeyPrefix();

        // When
        Optional<Post> postOptional = postRepository.findByObjectS3KeyPrefix(objectS3KeyPrefix);

        // Then
        assertTrue(postOptional.isPresent());
    }

    @Test
    public void updatePostInfo_and_insertInteractionInfo ()
    {
//        Given
        Post postIn = new Post();
        postIn.setObjectS3KeyPrefix("posts/raw/f40b62c3-07a0-4d07-9277-60ab4672d85b_v");
        postIn.setObjectS3KeySuffix(".mp4");
        Post retrievalPost = post;

//        When
        retrievalPost.setStatus("active");
        retrievalPost.setS3VideoUri(postIn.getObjectS3KeyPrefix()+ "." + postIn.getObjectS3KeySuffix());
        retrievalPost.setObjectS3KeySuffix(postIn.getObjectS3KeySuffix());
        this.postRepository.save(retrievalPost);
        List<PostAsset> postAssetList = this.postAssetRepo.findActiveByPostId(retrievalPost.getId());
        PostInteractionCreatedDto postInteractionCreatedDto = postInteractionPort.postCreated(retrievalPost.getId());

//        Then
        assertNotNull(postAssetList);
        assertNotNull(postInteractionCreatedDto);
        assertEquals(retrievalPost.getStatus(), "active");
    }

    @Test
    public void publishEventWhenAllPersisted()
    {
//        Given
        Post savedPost = post;
        savedPost.setStatus("active");
        savedPost.setObjectS3KeySuffix("mp4");
        savedPost.setS3VideoUri(post.getObjectS3KeyPrefix()+".mp4");
        PostInteractionCreatedDto postInteractionCreatedDto = new PostInteractionCreatedDto();
        postInteractionCreatedDto.setBoostedAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));

//        When
        List<PostAsset> postAssetList = this.postAssetRepo.findActiveByPostId(savedPost.getId());
        EventPostCreated eventPostCreated = EventPostCreated.builder()
                .postId(savedPost.getId())
                .thumbnailUrl(getThumbnail(postAssetList))
                .ImageUrls(getImagesUri(postAssetList).isEmpty()? null : getImagesUri(postAssetList).get())
                .videoUrl(savedPost.getS3VideoUri())
                .creatorId(savedPost.getCreatorId())
                .timeStamp(OffsetDateTime.now().toEpochSecond())
                .boostedAt(postInteractionCreatedDto.getBoostedAt())
                .build();

        this.publisher.publishEvent(eventPostCreated);

//        Then
        ArgumentCaptor<EventPostCreated> captor = ArgumentCaptor.forClass(EventPostCreated.class);
        verify(publisher).publishEvent(captor.capture());

        EventPostCreated event = captor.getValue();

        assertEquals(post.getId(), event.getPostId());
        assertEquals(post.getCreatorId(), event.getCreatorId());
        assertEquals(post.getS3VideoUri(), event.getVideoUrl());
        assertEquals(postInteractionCreatedDto.getBoostedAt(), event.getBoostedAt());
    }




}
