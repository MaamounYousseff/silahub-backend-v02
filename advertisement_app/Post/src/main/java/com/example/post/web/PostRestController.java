package com.example.post.web;


import com.example.post.logic.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/post/v0")
public class PostRestController
{

    @Autowired
    private PostService postService;

    @PostMapping("/create")
    public void createPost(@Valid @RequestBody PostCreateRequest postCreateRequest) {
        this.postService.createPost(postCreateRequest);
    }



//    just for test
    @GetMapping("/test")
    public void asd()
    {
        UUID postId = UUID.fromString("e6efcf6e-9c81-4002-a90e-4b4d7b52b388");

        this.postService.postCreated(postId);
    }
}
