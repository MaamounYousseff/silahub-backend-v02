package com.example.feed.domain.exception;



public class FeedPostNotExistException extends RuntimeException
{
    public FeedPostNotExistException()
    {
        super("Feed Post Limit Exceeded");
    }
}
