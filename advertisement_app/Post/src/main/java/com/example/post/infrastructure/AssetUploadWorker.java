package com.example.post.infrastructure;

import com.example.feed.api.FeedAssetUploadedPort;
import com.example.post.logic.PostAssetService;
import com.example.post.shared.S3EventNotification;
import com.example.post.shared.S3EventRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import java.util.List;
import com.example.post.domain.PostAsset;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;

import static com.example.post.Constant.BUCKET_NAME;
import static com.example.post.Constant.S3_DELIMINETER;

@Service
@Slf4j
public class AssetUploadWorker {

    @Autowired
    private PostAssetService postAssetService;
    @Autowired
    private SqsClient sqsClient;
    @Autowired
    @Qualifier("awsSqsObjectMapper")
    private ObjectMapper objectMapper;
    @Autowired
    private FeedAssetUploadedPort feedAssetUploadedPort;
    private static final String REGION = "eu-north-1";

    private final String sqsQueueUrl = "https://sqs.eu-north-1.amazonaws.com/418962810364/post_service_asset_upload";

    // PHASE 1: POLL THE MESSAGES EVERY 20 s
    @Scheduled(fixedRate = 15000)
    public void consumeAssetUploadMessages() {
        log.info("Asset Upload Worker - Starting to poll messages");
        try {
            // PHASE 2: GET THE MESSAGES
            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(sqsQueueUrl)
                    .maxNumberOfMessages(10) // Get up to 10 messages at a time
                    .waitTimeSeconds(5) // Long polling for 5 seconds
                    .build();

            List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();

            if (messages.isEmpty()) {
                log.info("No messages in queue: " + sqsQueueUrl);
                return;
            }

            for (Message message : messages) {
                log.info("Received message from SQS: " + message.body());

                String messageBody = message.body();
                S3EventNotification eventNotification = objectMapper.readValue(messageBody, S3EventNotification.class);

                processAssetMessage(eventNotification);

                // PHASE 3: DELETE THE MESSAGE
                deleteMessage(message);
            }

        } catch (SqsException e) {
            log.error("Error consuming messages from SQS: " + e.awsErrorDetails().errorMessage());
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void processAssetMessage(S3EventNotification eventNotification) {
        try {
            S3EventRecord record = eventNotification.getRecords().get(0);

            // Check if the event name is ObjectCreated:CompleteMultipartUpload
            if (!record.getEventName().equals("ObjectCreated:Put"))
                throw new RuntimeException("Invalid event Name");

            var objectKey = record.getS3().getObject().getKey();

            String[] str = objectKey.split("\\.");
            if (str.length < 2)
                throw new RuntimeException("Invalid asset prefix format " );

            var s3AssetPrefix = str[0];
            var s3AssetSuffix = str[1];
            var assetUrl = String.format("https://%s.s3.%s.amazonaws.com/%s.%s",
                    BUCKET_NAME,
                    REGION,
                    s3AssetPrefix,
                    s3AssetSuffix);

            PostAsset postAsset = this.postAssetService.findByS3AssetPrefix(s3AssetPrefix);
            postAsset.setS3AssetPrefix(s3AssetPrefix);
            postAsset.setS3AssetSuffix(s3AssetSuffix);
            postAsset.setS3AssetUrl(assetUrl);
            this.postAssetService.update(postAsset);
            this.feedAssetUploadedPort.assetUploaded(postAsset.getPostId(), postAsset.getS3AssetUrl(), postAsset.getType());

        } catch (Exception e) {
            log.error("Error processing asset message: " + e.getMessage());
        }
    }

    private void deleteMessage(Message message) {
        try {
            DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                    .queueUrl(sqsQueueUrl)
                    .receiptHandle(message.receiptHandle())
                    .build();

            sqsClient.deleteMessage(deleteMessageRequest);
            log.info("Message deleted from SQS: " + message.messageId());

        } catch (SqsException e) {
            log.error("Error deleting message from SQS: " + e.awsErrorDetails().errorMessage());
        }
    }
}

