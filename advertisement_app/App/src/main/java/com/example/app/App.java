package com.example.app;


import com.example.feed.FeedConfig;
import com.example.interaction.InteractionConfig;
import com.example.post.PostConfig;
import com.example.scoring.ScoringConfig;
import com.example.shared.SharedConfig;
import com.example.test.TestConfig;
import com.example.useradmin.UserAdminConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({InteractionConfig.class, SharedConfig.class, FeedConfig.class, ScoringConfig.class, UserAdminConfig.class, PostConfig.class, TestConfig.class})
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}