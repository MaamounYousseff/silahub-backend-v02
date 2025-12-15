package com.example.post.infrastructure;

import com.example.post.logic.PostService;
import org.springframework.stereotype.Service;

@Service
public class S3EventQueueListener {

    private PostService postService;
//@ NOT TESTED
//@ NOT IMPLEMENTED
//    @SqsListener("s3-upload-events-queue")
    public void onMessage(String eventJson) {
//        TODO.. extract the postId from the eventJson then pass it to the postService
//        this.postService.postCreated(postId);
//        Handle this one
//        TODO ..
//         CHANGE THE STATUS OF THIS POST IN THE DATABASE
//         THEN
//         PUBLISH A EVENT POSTCREATED
    }
}
