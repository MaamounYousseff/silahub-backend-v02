package com.example.shared.domain.event.interaction;


public interface InteractionEventListener
{
    void onFeedPostClicked(InteractionEventPostClicked eventClick);
    void onFeedPostLiked(InteractionEventToggleLike eventLike);
    void onFeedPostUpvoted(InteractionEventToggleUpvote eventUpvote);
    void onFeedPostWatched(InteractionEventPostWatched eventWatchTime);
}
