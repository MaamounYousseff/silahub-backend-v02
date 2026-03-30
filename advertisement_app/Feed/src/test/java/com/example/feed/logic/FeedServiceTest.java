package com.example.feed.logic;

import com.example.feed.domain.model.FeedPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = {
        com.example.feed.FeedConfig.class,
        com.example.shared.SharedConfig.class,
        com.example.shared_module_test.TestUserContext.class
})
@ComponentScan("com.example.feed")
class FeedServiceTest {

    @Autowired
    private FeedService feedService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void addNewAsset()
    {
        UUID postId = UUID.randomUUID();
        String imageUrl = "imageUrlTest";
        String thumbnailUrl = "thumbnailUrlTest";

        // -----------------------------
        // Case 1: Post does NOT exist
        // -----------------------------

        // Add image
        assertTrue(feedService.addNewAsset(postId, imageUrl, "image"));

        FeedPost post = mongoTemplate.findById(postId, FeedPost.class);
        assertNotNull(post, "Post should be created");
        assertEquals("pending", post.getStatus(), "Status should be pending");
        assertTrue(post.getImageUrls().contains(imageUrl), "ImageUrls should contain the new image");

        // Add thumbnail
        assertTrue(feedService.addNewAsset(postId, thumbnailUrl, "thumbnail"));
        post = mongoTemplate.findById(postId, FeedPost.class);
        assertEquals(thumbnailUrl, post.getThumbnailUrl(), "Thumbnail should be set");
        assertEquals("pending", post.getStatus(), "Status should remain pending");

        // -----------------------------
        // Case 2: Post already exists
        // -----------------------------
        String newImage = "newImage";
        String newThumbnail = "newThumbnail";

        // Update image
        assertTrue(feedService.addNewAsset(postId, newImage, "image"));
        post = mongoTemplate.findById(postId, FeedPost.class);
        assertTrue(post.getImageUrls().contains(newImage), "New image should be added");
        assertEquals("pending", post.getStatus(), "Status should not change");

        // Update thumbnail
        assertTrue(feedService.addNewAsset(postId, newThumbnail, "thumbnail"));
        post = mongoTemplate.findById(postId, FeedPost.class);
        assertEquals(newThumbnail, post.getThumbnailUrl(), "Thumbnail should be updated");
        assertEquals("pending", post.getStatus(), "Status should not change");

    }
}