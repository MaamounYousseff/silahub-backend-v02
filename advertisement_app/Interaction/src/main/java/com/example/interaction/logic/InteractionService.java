package com.example.interaction.logic;


import com.example.interaction.domain.command.ClickPost;
import com.example.interaction.domain.command.ToggleLikePost;
import com.example.interaction.domain.command.ToggleUpvotePost;
import com.example.interaction.domain.command.WatchPost;
import com.example.shared.domain.event.interaction.InteractionEventPostClicked;
import com.example.shared.domain.event.interaction.InteractionEventPostWatched;
import com.example.shared.security.CurrentUserContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.example.interaction.domain.Constant.*;

@Service
@Slf4j
public class InteractionService
{
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private CurrentUserContext currentUserContext;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ObjectMapper objectMapper;



    public void feedPostClicked(UUID feedPostId) throws JsonProcessingException {
        ClickPost clickPost = new ClickPost(feedPostId);
        this.jmsTemplate.convertAndSend(  TOPIC_CLICK_NAME , objectMapper.writeValueAsString(clickPost) );
        this.publisher.publishEvent(new InteractionEventPostClicked(feedPostId));
    }

    public void feedPostWatched(UUID feedPostId, Long watchTime) throws JsonProcessingException {
        WatchPost watchPost = new WatchPost(feedPostId,watchTime);
        this.jmsTemplate.convertAndSend(  TOPIC_WATCH_NAME , objectMapper.writeValueAsString(watchPost) );
        this.publisher.publishEvent(new InteractionEventPostWatched(feedPostId,watchTime));
    }

    public void feedPostLikeToggle(UUID feedPostId) throws JsonProcessingException {
       ToggleLikePost toggleLikePost = new ToggleLikePost(feedPostId,currentUserContext.getUserId());
       jmsTemplate.convertAndSend( TOPIC_LIKE_NAME , objectMapper.writeValueAsString(toggleLikePost) );
  }

    public void feedPostUpvoteToggle(UUID feedPostId) throws JsonProcessingException {
        ToggleUpvotePost toggleLikePost = new ToggleUpvotePost(feedPostId,currentUserContext.getUserId());
        jmsTemplate.convertAndSend( TOPIC_UPVOTE_NAME , objectMapper.writeValueAsString(toggleLikePost) );
    }


}
