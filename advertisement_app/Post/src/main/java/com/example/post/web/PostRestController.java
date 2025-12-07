package com.example.post.web;


import com.example.post.logic.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
}
