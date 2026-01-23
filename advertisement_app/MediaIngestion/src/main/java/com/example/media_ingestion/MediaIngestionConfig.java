package com.example.media_ingestion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

import static com.example.media_ingestion.MediaIngestionService.*;

@Configuration
@EnableScheduling
@ComponentScan("com.example.media_ingestion")
public class MediaIngestionConfig
{

    @Bean
    public AwsBasicCredentials awsBasicCredentials()
    {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY);
        return awsCreds;
    }

    @Bean
    public S3AsyncClient s3Client(AwsBasicCredentials awsBasicCredentials) {
        S3AsyncClient s3AsyncClient = S3AsyncClient.builder()
                .region(Region.of(AWS_REGION))
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .build();

        return s3AsyncClient;
    }

    @Bean
    public S3TransferManager transferManager(S3AsyncClient s3Client) {
        return S3TransferManager.builder()
                .s3Client(s3Client)
                .build();
    }

}
