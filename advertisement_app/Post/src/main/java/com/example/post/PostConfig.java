package com.example.post;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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


    @Bean
    @Primary
    public ObjectMapper objectMapper(ObjectMapper defaultMapper) {
        // Return the auto-configured mapper that Spring Boot provides
        // This preserves all Spring Boot customizations (JavaTimeModule, JDK8Module, etc.)
        return defaultMapper;
    }

    @Bean
    @Qualifier("awsSqsObjectMapper")
    public ObjectMapper awsSqsObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
