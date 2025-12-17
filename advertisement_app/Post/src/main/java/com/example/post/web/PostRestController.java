package com.example.post.web;


import com.example.post.domain.Post;
import com.example.post.logic.PostService;
import com.example.shared.SilahubResponse;
import com.example.shared.SilahubResponseUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/post/v0")
public class PostRestController
{

    @Autowired
    private PostService postService;

    @PostMapping("/create")
    public ResponseEntity<SilahubResponse> createPost(@Valid @RequestBody PostCreateRequest postCreateRequest) {
        Post post = this.postService.createPost(postCreateRequest);
        return ResponseEntity.ok(SilahubResponseUtil.success(post, "Post created successfully", Map.of()));
    }

}
