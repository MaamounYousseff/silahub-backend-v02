package com.example.post.logic;

import com.example.shared.domain.event.post.PostEventPostCreated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class AwsS3Listener
{
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

//   Todo :
//    @SqsListener("s3-upload-events-queue")
    public void handleEvent(String message)
    {
//        System.out.println("receive event" + message);
        this.applicationEventPublisher.publishEvent(new PostEventPostCreated(UUID.randomUUID(), UUID.randomUUID(), Instant.now().getEpochSecond()));
    }
}
