package com.example.post.web;


import com.example.post.domain.Post;
import com.example.post.logic.PostService;
import com.example.post.logic.PreSignUrlsResult;
import com.example.shared.SilahubResponse;
import com.example.shared.SilahubResponseUtil;
import com.example.shared.security.CurrentUserContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

import static com.example.post.web.PostMapper.fromPostIntentCreateRequest;


@RestController
@RequestMapping("/api/post/v0")
@CrossOrigin(origins = "http://localhost:5173")
public class PostRestController
{
    @Autowired
    private PostService postService;
    @Autowired
    private CurrentUserContext currentUserContext;

    @PostMapping("/create")
    public ResponseEntity<SilahubResponse> createPost(@Valid @RequestBody PostIntentCreateRequest postIntentCreateRequest) {
        Post post = this.postService.createPost(postIntentCreateRequest);
        return ResponseEntity.ok(SilahubResponseUtil.success(post, "Post created successfully", Map.of()));
    }


    @PostMapping("/post_intent")
    public ResponseEntity<SilahubResponse> createPostIntent(@Valid @RequestBody PostIntentCreateRequest postIntentCreateRequest)
    {
        PreSignUrlsResult preSignUrlsResult = this.postService.createPostIntent(postIntentCreateRequest);

        return ResponseEntity.ok(SilahubResponseUtil.success(preSignUrlsResult, "Post Intent created successfully", Map.of() ));
    }

    @GetMapping("/hi")
    public String hi()
    {
        return "hi";
    }

}
