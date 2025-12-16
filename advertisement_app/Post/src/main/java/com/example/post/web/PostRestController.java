package com.example.post.web;


import com.example.post.logic.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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


    @GetMapping("/test")
    public void asd()
    {
        UUID creatorId = UUID.fromString("f07cc7e5-842c-409e-a0a4-75615e14fe93");
        this.postService.postCreated(creatorId);

    }



}
