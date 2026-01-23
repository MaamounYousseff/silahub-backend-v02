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
        com.example.post.PostConfig.class,
        com.example.shared.SharedConfig.class
})
@ComponentScan("com.example.post")
class PostRestControllerTest
{
    @Autowired
    private PostService postService;





}