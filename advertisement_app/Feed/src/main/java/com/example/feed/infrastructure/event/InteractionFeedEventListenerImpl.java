package com.example.feed.infrastructure.event;

import com.example.feed.logic.FeedService;
import com.example.shared.interaction.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class InteractionFeedEventListenerImpl implements InteractionEventListener
{
    @Autowired
    private FeedService feedService;

    @Override
    @EventListener
    public void onFeedPostClicked(InteractionEventPostClicked eventClick) {
        System.out.println("Feed receive click");
    }

    @Override
    @EventListener
    public void onFeedPostLiked(InteractionEventToggleLike eventLike) {
        System.out.println("Feed receive like");
        this.feedService.processLike(eventLike);
    }

    @Override
    @EventListener
    public void onFeedPostUpvoted(InteractionEventToggleUpvote eventUpvote) {
        System.out.println("Feed receive upvote");
        this.feedService.processUpvote(eventUpvote);
    }

    @Override
    @EventListener
    public void onFeedPostWatched(InteractionEventPostWatched eventWatchTime) {
        System.out.println("Feed receive watch");
    }


}
