package com.example.post.logic;


import com.example.post.domain.mapper.PostMapper;
import com.example.post.domain.model.Post;
import com.example.post.domain.repo.PostRepository;
import com.example.post.web.PostCreateRequest;
import com.example.shared.security.CurrentUserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

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
