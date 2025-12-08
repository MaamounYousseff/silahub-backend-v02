package com.example.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class Producer {
    @Autowired
    private JmsTemplate jmsTemplate;
    private static final String QUEUE_NAME = "testQueue";

    public void sendMessage(String message) {
        jmsTemplate.convertAndSend(QUEUE_NAME, message);
        System.out.println("Message sent: " + message);
    }

}
