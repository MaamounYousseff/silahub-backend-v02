package com.example.post.logic;


import com.example.post.domain.PostMapper;
import com.example.post.domain.Post;
import com.example.post.domain.PostRepository;
import com.example.post.web.PostCreateRequest;
import com.example.shared.security.CurrentUserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService
{
    @Autowired
    private CurrentUserContext currentUserContext;
    @Autowired
    PostRepository postRepository;

    public void createPost(PostCreateRequest postRequest)
    {
        Post post = PostMapper.fromPostCreateRequest(postRequest,currentUserContext.getUserId());
        postRepository.save(post);
    }
}
