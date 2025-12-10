package com.example.scoring.infrastructure.event;

import com.example.scoring.logic.ScoringService;
import com.example.shared.domain.event.interaction.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class InteractionScoreEventListenerImpl implements InteractionEventListener
{
    @Autowired
    private ScoringService scoringService;

    @Override
    @EventListener
    public void onFeedPostClicked(InteractionEventPostClicked eventClick) {
        System.out.println("Score receive click");
        scoringService.processClick(eventClick);
    }

    @Override
    @EventListener
    public void onFeedPostWatched(InteractionEventPostWatched eventWatchTime) {
        System.out.println("Score receive watch");
        scoringService.processWatchTime(eventWatchTime);
    }

    @Override
    @EventListener
    public void onFeedPostLiked(InteractionEventToggleLike eventLike) {
        System.out.println("Score receive like");
        scoringService.processLike(eventLike);
    }

    @Override
    @EventListener
    public void onFeedPostUpvoted(InteractionEventToggleUpvote eventUpvote) {
        System.out.println("Score receive upvote");
        scoringService.processUpvote(eventUpvote);
    }
}
