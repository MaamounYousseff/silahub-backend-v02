package com.example.feed.domain.exception;

public class FeedPostLimitExceededException extends RuntimeException
{
    public FeedPostLimitExceededException()
    {
        super("Feed Post Limit Exceeded");
    }
}
