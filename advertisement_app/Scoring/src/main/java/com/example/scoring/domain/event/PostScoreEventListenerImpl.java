package com.example.scoring.domain.event;

import com.example.shared.domain.event.post.PostEventListener;
import com.example.shared.domain.event.post.PostEventPostCreated;
import org.springframework.context.event.EventListener;

public class PostScoreEventListenerImpl implements PostEventListener
{
    @Override
    @EventListener
    public void onPostCreated(PostEventPostCreated eventPostCreated) {
//        NOT TESTED
        System.out.println("Scpre receive post created");
    }
}
