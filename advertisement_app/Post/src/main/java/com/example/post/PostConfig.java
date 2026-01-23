package com.example.post;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
@ComponentScan("com.example.post")
@EntityScan("com.example.post.domain")
@EnableJpaRepositories("com.example.post.domain")
@EnableScheduling // Enable scheduling for @Scheduled methods
public class PostConfig
{
//    @Value("${aws.region}")
    private String awsRegion = "eu-north-1";

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .region(Region.of(awsRegion))
                .build();
    }


}
