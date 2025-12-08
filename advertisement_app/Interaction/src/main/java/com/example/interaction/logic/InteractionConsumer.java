package com.example.interaction.logic;

import com.example.interaction.domain.command.ToggleLikePost;
import com.example.interaction.domain.repo.PostInteractionRepo;
import com.example.interaction.domain.repo.PostLikeRepo;
import com.example.shared.domain.event.interaction.ToggleLikeAction;
import com.example.shared.domain.event.interaction.InteractionEventToggleLike;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;


@Service
@Slf4j
public class InteractionConsumer
{
    @Autowired
    private PostInteractionRepo postInteractionsRepo;
    @Autowired
    private PostLikeRepo postLikeRepo;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ApplicationEventPublisher publisher;

    public static final String TOPIC_NAME = "post_like_toggle";


    @JmsListener(destination = TOPIC_NAME)
    @Transactional
    public void postToggleLike(String json) {

        try {
            ToggleLikePost payload = objectMapper.readValue(json, ToggleLikePost.class);
            UUID explorerId = payload.getExplorerId();
            UUID postId = payload.getPostId();

            ToggleLikeAction action = postLikeRepo.toggleLike(explorerId, postId);

            adjustPostLikeCount(postId, action);

            publishToggleLikeEvent(postId, action);

        } catch (JsonProcessingException e) {
            log.error("Failed to parse ToggleLikePost JSON", e);
            throw new RuntimeException(e); // Ensure rollback
        }
    }

    private void adjustPostLikeCount(UUID postId, ToggleLikeAction action) {
        switch (action) {
            case INSERT, UPDATE_LIKED -> postInteractionsRepo.incrementLikes(postId);
            case UPDATE_UNLIKED -> postInteractionsRepo.decrementLikes(postId);
        }
    }

    private void publishToggleLikeEvent(UUID postId, ToggleLikeAction action) {
        publisher.publishEvent(
                new InteractionEventToggleLike(postId, postId, action, OffsetDateTime.now())
        );
    }

}
