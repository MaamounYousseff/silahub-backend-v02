package com.example.shared.domain.event.interaction;


public interface InteractionEventListener
{
    void onFeedPostClicked(InteractionEventClick eventClick);
    void onFeedPostLiked(InteractionEventLike eventLike);
    void onFeedPostUpvoted(InteractionEventUpvote eventUpvote);
    void onFeedPostWatched(InteractionEventWatchTime eventWatchTime);
}
