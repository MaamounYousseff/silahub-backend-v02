package com.example.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/test")
public class TestRestController
{
    @Autowired
    private TestArrayRepo repo;
    @Autowired
    private Producer messageProducer;

    @GetMapping("/aa")
    public String testArray(){
        TestArray a = repo.find();
        return a.toString();
    }



    @GetMapping("/send")
    public String sendMessage() {
        String message = "Hello from ActiveMQ!";
        messageProducer.sendMessage(message);
        return "Message sent to ActiveMQ: " + message;
    }
}
