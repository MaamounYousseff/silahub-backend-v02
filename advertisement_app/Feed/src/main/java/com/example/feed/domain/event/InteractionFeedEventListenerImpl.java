package com.example.feed.domain.event;

import com.example.shared.domain.event.interaction.InteractionEventClick;
import com.example.shared.domain.event.interaction.InteractionEventToggleLike;
import com.example.shared.domain.event.interaction.InteractionEventToggleUpvote;
import com.example.shared.domain.event.interaction.InteractionEventWatchTime;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class InteractionFeedEventListenerImpl implements com.example.shared.domain.event.interaction.InteractionEventListener
{

    @Override
    @EventListener
    public void onFeedPostClicked(InteractionEventClick eventClick) {
        System.out.println("Feed receive click");
    }

    @Override
    @EventListener
    public void onFeedPostLiked(InteractionEventToggleLike eventLike) {
        System.out.println("Feed receive like");
    }

    @Override
    @EventListener
    public void onFeedPostUpvoted(InteractionEventToggleUpvote eventUpvote) {
        System.out.println("Feed receive upvote");
    }

    @Override
    @EventListener
    public void onFeedPostWatched(InteractionEventWatchTime eventWatchTime) {
        System.out.println("Feed receive watch");
    }


}
