package com.example.post.web;

import com.example.post.logic.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Commit;

import java.util.List;
import java.util.UUID;

@SpringBootTest(classes = {
        com.example.post.PostConfig.class
})
@ComponentScan("com.example.post")
class PostRestControllerTest
{
    @Autowired
    private PostService postService;


    @Test
    @Commit
    void createPost() {
        // Arrange
        PostCreateRequest req = new PostCreateRequest();
        req.setThumbnailUrl("https://upload.wikimedia.org/wikipedia/commons/a/a3/June_odd-eyed-cat.jpg");
        req.setVideoUrl("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
        req.setImageUrls(List.of(
                "https://upload.wikimedia.org/wikipedia/commons/a/a3/June_odd-eyed-cat.jpg",
                "https://upload.wikimedia.org/wikipedia/commons/3/3f/Fronalpstock_big.jpg"
        ));

        req.setTitle("My Post");
        req.setDescription("This is a test post");
        req.setContentType("image");
        req.setIsVisible(true);
        postService.createPost(req);

    }



}