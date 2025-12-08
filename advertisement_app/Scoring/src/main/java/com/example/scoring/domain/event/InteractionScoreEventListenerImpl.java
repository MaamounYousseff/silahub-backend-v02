package com.example.scoring.domain.event;

import com.example.shared.domain.event.interaction.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class InteractionScoreEventListenerImpl implements InteractionEventListener
{

    @Override
    @EventListener
    public void onFeedPostClicked(InteractionEventPostClicked eventClick) {
        System.out.println("Score receive click");
    }

    @Override
    @EventListener
    public void onFeedPostLiked(InteractionEventToggleLike eventLike) {
        System.out.println("Score receive like");
    }

    @Override
    @EventListener
    public void onFeedPostUpvoted(InteractionEventToggleUpvote eventUpvote) {
        System.out.println("Score receive upvote");
    }

    @Override
    @EventListener
    public void onFeedPostWatched(InteractionEventPostWatched eventWatchTime) {
        System.out.println("Score receive watch");
    }


}
