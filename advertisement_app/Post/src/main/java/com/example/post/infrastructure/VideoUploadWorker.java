package com.example.post.infrastructure;


import com.example.post.domain.Post;
import com.example.post.domain.PostRepository;
import com.example.post.shared.S3EventNotification;
import com.example.post.shared.S3EventRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import java.util.List;
import java.util.Optional;
import static com.example.post.Constant.BUCKET_NAME;
import static com.example.post.Constant.S3_DELIMINETER;

@Service
@Slf4j
public class VideoUploadWorker {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private SqsClient sqsClient;
    @Autowired
    @Qualifier("awsSqsObjectMapper")
    private ObjectMapper objectMapper;


    private String sqsQueueUrl = "https://sqs.eu-north-1.amazonaws.com/418962810364/post_service_video_upload";

    // PHASE 1: POLL THE MESSAGES EVERY 20s
    @Scheduled(fixedRate = 20000)
    public void consumeVideoUploadMessages() {
        log.info("Video Upload Worker - Starting to poll messages");
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

                processVideoMessage(eventNotification);

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

    private void processVideoMessage(S3EventNotification eventNotification) {
        try {
            S3EventRecord record = eventNotification.getRecords().get(0);

            // Check if the event name is :
            if (!record.getEventName().equals("ObjectCreated:CompleteMultipartUpload"))
                throw new RuntimeException("Invalid event Name");

            // Split the object key into prefix and suffix suffix is the container
            var objectKey = record.getS3().getObject().getKey();
            String str[] = objectKey.split("\\.");
            var objectKeyPrefix = str[0];
            var objectKeySuffix = str[1];
            var videoUri = BUCKET_NAME + S3_DELIMINETER + objectKeyPrefix + "." + objectKeySuffix;

            // check if prefix exist for a post
            Optional<Post> postOptional = this.postRepository.findByObjectS3KeyPrefix(objectKeyPrefix);
            if (!Post.postExist(postOptional))
                throw new RuntimeException("There was no post for this key : " + objectKeyPrefix);
            Post post = postOptional.get();

            // update post status to draft AND UPDATE THE SUFFIX ALSO
            int row = this.postRepository.update(post.getId(), videoUri, objectKeySuffix, "draft");
            if (row == 0)
                throw new RuntimeException("Failed To update Post Status to draft \n Post Id: " + post.getId());

        } catch (Exception e) {
            log.error("Error processing video message: " + e.getMessage());
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





