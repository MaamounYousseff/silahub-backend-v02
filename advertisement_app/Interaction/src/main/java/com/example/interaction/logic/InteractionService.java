package com.example.interaction.logic;


import com.example.interaction.domain.repo.PostInteractionsRepo;
import com.example.shared.domain.event.interaction.InteractionEventClick;
import com.example.shared.domain.event.interaction.InteractionEventLike;
import com.example.shared.domain.event.interaction.InteractionEventUpvote;
import com.example.shared.domain.event.interaction.InteractionEventWatchTime;
import com.example.shared.security.CurrentUserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class InteractionService
{
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private CurrentUserContext currentUserContext;


    public void feedPostClicked(UUID feedPostId){
//      TODO  make the producer publish  to  red panda

//        publish a event to EventBus :
        this.publisher.publishEvent(new InteractionEventClick(feedPostId, Instant.now().getEpochSecond()));
    }

    public void feedPostLiked(UUID feedPostId){
//      TODO  make the producer publish  to  red panda

//        publish a event to EventBus
        this.publisher.publishEvent(new InteractionEventLike(feedPostId, currentUserContext.getUserId() ,Instant.now().getEpochSecond()));
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
