package com.example.post;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan("com.example.post")
@EntityScan("com.example.post.domain.model")
@EnableJpaRepositories("com.example.post.domain.repo")
public class PostConfig
{

}
