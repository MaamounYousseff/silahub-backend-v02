package com.example.post.infrastructure;

import org.springframework.stereotype.Service;

@Service
public class S3EventQueueListener {

//@ NOT TESTED
//@ NOT IMPLEMENTED
//    @SqsListener("s3-upload-events-queue")
    public void onMessage(String eventJson) {
//        Handle this one
//        TODO ..
//         CHANGE THE STATUS OF THIS POST IN THE DATABASE
//         THEN
//         PUBLISH A EVENT POSTCREATED
    }
}
