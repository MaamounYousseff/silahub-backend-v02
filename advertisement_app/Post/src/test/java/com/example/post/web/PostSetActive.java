package com.example.post.web;

import com.example.post.domain.Post;
import com.example.post.domain.PostRepository;
import com.example.post.logic.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Commit;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = {
        com.example.post.PostConfig.class,
        com.example.shared.SharedConfig.class,
        com.example.shared_module_test.TestUserContext.class

})
@ComponentScan("com.example.post")
public class PostSetActive
{
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostService postService;

    @Test
    void getPostWithObjectKeyPrefix_returnsPost_whenPrefixMatches() {
        // Given
        String objectKeyPrefix = "posts/raw/Ch06_The+Relational+Algebra+and+Relational+Calculus+-+Voice+-+Week1";

        // When
        Optional<Post> result = postRepository.findByObjectS3KeyPrefix(objectKeyPrefix);
        Post post = result.get();

        // Then
        assertNotNull(post);
        assertTrue(post.getObjectS3KeyPrefix().startsWith(objectKeyPrefix));

    }



@Test
@Commit
    void updatePostStatus()
    {
//      Given
        Post post = new Post();

        post.setId(UUID.fromString("5fbdc2cf-6926-4d51-a749-9a1ac41b9166"));
        post.setCreatorId(UUID.fromString("cfd8a81c-7bae-4f3b-a8f2-f1e280e51b43"));

        post.setTitle("Ch06 – Relational Algebra & Calculus");
        post.setDescription("Week 1 voice lecture");
        post.setObjectS3KeyPrefix(
                "posts/raw/Ch06_The+Relational+Algebra+and+Relational+Calculus+-+Voice+-+Week1"
        );
        post.setObjectS3KeySuffix("mp4");

        post.setS3VideoUri(
                "amzn-s3-bucket-lb-01/posts/raw/Ch06_The+Relational+Algebra+and+Relational+Calculus+-+Voice+-+Week1.mp4"
        );
        post.setIsVisible(true);
        post.setImageCount((short) 0);
        post.setThumbnailCount((short) 0);

//      When
        post.setStatus("active");
        postRepository.save(post);

//      Then
        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        assertEquals("active", updatedPost.getStatus());
    }

//    Todo see what happen in exception case

    @Test
    void updatePostStatusToActive()
    {
//        Given
        Post post = new Post();

        post.setObjectS3KeyPrefix(
                "posts/raw/Ch06_The+Relational+Algebra+and+Relational+Calculus+-+Voice+-+Week1"
        );

//        When
        this.postService.postUploadCompleted(post);

//        Then
        Post postResult = this.postService.findByObjectS3KeyPrefix(post.getObjectS3KeyPrefix());
        assertEquals("active", postResult.getStatus());
    }
}
