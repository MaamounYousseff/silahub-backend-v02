package com.example.test;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.annotation.EnableJms;

@Configuration
@ComponentScan("com.example.test")
@EnableJpaRepositories("com.example.test")
@EntityScan("com.example.test")
public class TestConfig
{


}
