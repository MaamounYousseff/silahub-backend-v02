package com.example.test;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

    @JmsListener(destination = "testQueue")
    public void receiveMessage(String message) {
        System.out.println("Message received: " + message);
        // You can add any processing logic here
    }
}