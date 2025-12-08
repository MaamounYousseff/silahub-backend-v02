package com.example.interaction.logic;


import com.example.interaction.domain.command.ToggleLikePost;
import com.example.shared.domain.event.interaction.InteractionEventClick;
import com.example.shared.domain.event.interaction.InteractionEventUpvote;
import com.example.shared.domain.event.interaction.InteractionEventWatchTime;
import com.example.shared.security.CurrentUserContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

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

    public static final String TOPIC_NAME = "post_like_toggle";

    public void feedPostClicked(UUID feedPostId){
//      TODO  make the producer publish  to  red panda

//        publish a event to EventBus :
        this.publisher.publishEvent(new InteractionEventClick(feedPostId, Instant.now().getEpochSecond()));
    }

    public void feedPostLikeToggle(UUID feedPostId) throws JsonProcessingException {
       ToggleLikePost toggleLikePost = new ToggleLikePost(feedPostId,currentUserContext.getUserId());
       jmsTemplate.convertAndSend( TOPIC_NAME , objectMapper.writeValueAsString(toggleLikePost) );
  }

    public void feedPostUpvoted(UUID feedPostId){
//      TODO  make the producer publish  to  red panda

//        publish a event to EventBus
        this.publisher.publishEvent(new InteractionEventUpvote(feedPostId, currentUserContext.getUserId(), Instant.now().getEpochSecond()));
    }

    public void feedPostWatched(UUID feedPostId){
//      TODO  make the producer publish  to  red panda

//        publish a event to EventBus
        this.publisher.publishEvent(new InteractionEventWatchTime(feedPostId, currentUserContext.getUserId()));
    }
}
