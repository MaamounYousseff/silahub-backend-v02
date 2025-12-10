package com.example.interaction.logic;

import com.example.interaction.domain.command.ClickPost;
import com.example.interaction.domain.command.ToggleLikePost;
import com.example.interaction.domain.command.ToggleUpvotePost;
import com.example.interaction.domain.command.WatchPost;
import com.example.interaction.domain.repo.PostInteractionRepo;
import com.example.interaction.domain.repo.PostLikeRepo;
import com.example.interaction.domain.repo.PostUpvoteRepo;
import com.example.shared.domain.event.interaction.InteractionEventToggleUpvote;
import com.example.shared.domain.event.interaction.ToggleLikeAction;
import com.example.shared.domain.event.interaction.InteractionEventToggleLike;
import com.example.shared.domain.event.interaction.ToggleUpvoteState;
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

import static com.example.interaction.domain.Constant.*;


@Service
@Slf4j
public class InteractionConsumer
{
    @Autowired
    private PostInteractionRepo postInteractionsRepo;
    @Autowired
    private PostLikeRepo postLikeRepo;
    @Autowired
    private PostUpvoteRepo postUpvoteRepo;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ApplicationEventPublisher publisher;


    @JmsListener(destination = TOPIC_LIKE_NAME)
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
                new InteractionEventToggleLike(postId, postId, action )
        );
    }


    @JmsListener(destination = TOPIC_UPVOTE_NAME)
    @Transactional
    public void postToggleUpvote(String json) {

        try {
            ToggleUpvotePost payload = objectMapper.readValue(json, ToggleUpvotePost.class);
            UUID promoterId = payload.getPromoterId();
            UUID postId = payload.getPostId();

            ToggleUpvoteState action = postUpvoteRepo.toggleUpvote(promoterId, postId);

            adjustPostUpvoteCount(postId, action);

            publishToggleUpvoteEvent(postId, action);

        } catch (JsonProcessingException e) {
            log.error("Failed to parse ToggleUpvoteState JSON", e);
            throw new RuntimeException(e); // Ensure rollback
        }
    }

    private void adjustPostUpvoteCount(UUID postId, ToggleUpvoteState action) {
        switch (action) {
            case INSERT,  UPDATE_ADDED_UPVOTE-> postInteractionsRepo.incrementUpvotesAndBoost(postId);
            case UPDATE_REMOVED_UPVOTE -> postInteractionsRepo.decrementUpvotes(postId);
        }
    }

    private void publishToggleUpvoteEvent(UUID postId, ToggleUpvoteState action) {
        publisher.publishEvent(
                new InteractionEventToggleUpvote(postId, postId, action)
        );
    }



    @JmsListener(destination = TOPIC_WATCH_NAME)
    @Transactional
    public void postWatch(String json) throws JsonProcessingException {
        WatchPost watchPost = objectMapper.readValue(json, WatchPost.class);
        this.postInteractionsRepo.incrementWatchSeconds(watchPost.getPostId(),watchPost.getWatchTime());
    }


    @JmsListener(destination = TOPIC_CLICK_NAME)
    @Transactional
    public void postClick(String json) throws JsonProcessingException {
        ClickPost clickPost = objectMapper.readValue(json, ClickPost.class);
        this.postInteractionsRepo.incrementClick(clickPost.getPostId());
    }

}
