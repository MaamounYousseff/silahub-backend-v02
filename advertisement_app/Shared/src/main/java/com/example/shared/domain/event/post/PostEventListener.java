package com.example.shared.domain.event.post;

public interface PostEventListener
{
    void onPostCreated(PostEventPostCreated eventPostCreated);
}
