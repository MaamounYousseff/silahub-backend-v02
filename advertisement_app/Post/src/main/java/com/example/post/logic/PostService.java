package com.example.post.logic;


import com.example.interaction.api.PostInteractionCreatedDto;
import com.example.interaction.api.PostInteractionPort;
import com.example.post.web.PostMapper;
import com.example.post.domain.Post;
import com.example.post.domain.PostRepository;
import com.example.post.web.PostCreateRequest;
import com.example.shared.post.EventPostCreated;
import com.example.shared.security.CurrentUserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostService
{
    @Autowired
    private CurrentUserContext currentUserContext;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private PostInteractionPort postInteractionPort;

    public Post createPost(PostCreateRequest postRequest)
    {
        Post post = PostMapper.fromPostCreateRequest(postRequest,currentUserContext.getUserId());
        return postRepository.save(post);
    }

    @Transactional
    public void postCreated(UUID postId)
    {
        Optional<Post> postOptional = this.postRepository.findById(postId);

        if(!Post.postExist(postOptional))
            return;

        Post post = postOptional.get();

//        tightly couple with Interaction Context
        PostInteractionCreatedDto postInteractionCreatedDto = postInteractionPort.postCreated(post.getId());

        post.setStatus("active");
        this.postRepository.save(post);

        EventPostCreated eventPostCreated = EventPostCreated.builder()
                .postId(post.getId())
                .thumbnailUrl(post.getThumbnailUrl())
                .ImageUrls(post.getImageUrls())
                .videoUrl(post.getVideoUrl())
                .creatorId(post.getCreatorId())
                .timeStamp(OffsetDateTime.now().toEpochSecond())
                .boostedAt(postInteractionCreatedDto.getBoostedAt())
                .build();

        this.publisher.publishEvent(eventPostCreated);
    }
}
