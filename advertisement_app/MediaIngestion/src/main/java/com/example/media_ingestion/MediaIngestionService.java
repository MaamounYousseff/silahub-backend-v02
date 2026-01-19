package com.example.media_ingestion;

import com.example.media_ingestion.sqsevent.S3EventNotification;
import com.example.media_ingestion.sqsevent.S3EventRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.*;

import static com.example.media_ingestion.MediaIngestionHelper.*;
import static com.example.media_ingestion.MediaIngestionHelper.getTranscodinWorker360p;
import static com.example.media_ingestion.MediaIngestionHelper.getTranscodinWorker720pHQ;
import static com.example.media_ingestion.MediaIngestionHelper.getTranscodinWorker720pMQ;

@Slf4j
@Service
public class MediaIngestionService {

    public static final String S3_DELIMITER = "/";
    public static final String S3_BUCKET_NAME = "amzn-s3-bucket-lb-01";
    public static final String SQS_QUEUE_URL = "https://sqs.eu-north-1.amazonaws.com/418962810364/transcoding-queue";
    public static final String OUTPUT_PATH_1080P_HQ = "posts/processed/<file_name>/1080p_hq/";
    public static final String OUTPUT_PATH_720P_HQ = "posts/processed/<file_name>/720p_hq/";
    public static final String OUTPUT_PATH_720P_MQ = "posts/processed/<file_name>/720p_mq/";
    public static final String OUTPUT_PATH_360P_LQ = "posts/processed/<file_name>/360p_lq/";
    public static final String AWS_REGION = "eu-north-1";
    public static final String TEMP_CHUNK_DIR = "C:\\temp\\video_chunks";
    public static final String TEMP_FILE = "C:\\temp\\temp_video_processing.mp4";
    public static final String ACCESS_KEY = "AKIAWDDBBEX6NR6WXJRW";
    public static final String SECRET_KEY = "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";
    public static final int CHUNK_DURATION = 5;

    private final ObjectMapper objectMapper;
    private S3TransferManager transferManager;
    private SqsClient sqsClient;
    private FFmpeg ffmpeg;
    private FFprobe ffprobe;
    private CountDownLatch countDownLatch;

    private String fileName;
    private double duration;
    private String objectKey;

    private MediaIngestionWorker w1080;
    private MediaIngestionWorker w720HQ;
    private MediaIngestionWorker w720MQ;
    private MediaIngestionWorker w360;

    @Autowired
    public MediaIngestionService(@Qualifier("awsSqsObjectMapper") ObjectMapper objectMapper, S3TransferManager s3TransferManager) {
        this.objectMapper = objectMapper;
        this.transferManager = s3TransferManager;
    }

    @PostConstruct
    public void init() throws IOException {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY);

        sqsClient = SqsClient.builder()
                .region(Region.of(AWS_REGION))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();

        ffmpeg = new FFmpeg("ffmpeg");
        ffprobe = new FFprobe("ffprobe");

        Files.createDirectories(Paths.get(TEMP_CHUNK_DIR));
    }

    @Scheduled(fixedDelay = 5000)
    public void processMessages() {
        log.info("Polling SQS queue for messages");

        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(SQS_QUEUE_URL)
                .maxNumberOfMessages(1)
                .waitTimeSeconds(10)
                .build();

        List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

        for (Message message : messages) {
            try {
                log.info("Received message: {}", message.body());
                processMessage(message);

                DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                        .queueUrl(SQS_QUEUE_URL)
                        .receiptHandle(message.receiptHandle())
                        .build();
                sqsClient.deleteMessage(deleteRequest);

                log.info("Message processed and deleted successfully");
            } catch (Exception e) {
                log.error("Error processing message", e);
            }
        }
    }

    private void processMessage(Message message) throws Exception {
        S3EventNotification eventNotification = objectMapper.readValue(message.body(), S3EventNotification.class);

        for (S3EventRecord record : eventNotification.getRecords()) {
            String bucketName = record.getS3().getBucket().getName();
            objectKey = record.getS3().getObject().getKey();
            log.info("Processing S3 object: bucket={}, key={}", bucketName, objectKey);

            log.info("Downloading from S3: {}/{}", bucketName, objectKey);
            MediaIngestionHelper.downloadFromS3(bucketName, objectKey, TEMP_FILE, transferManager);
            log.info("Download completed: {}", TEMP_FILE);


            double duration = getVideoDuration(TEMP_FILE, ffprobe);
            log.info("Video duration: {} seconds", duration);

            String objectKeyName = getObjectKeyName(objectKey);
            w1080 = getTranscodinWorker1080p(transferManager,TEMP_FILE,duration,objectKeyName);
            w720HQ = getTranscodinWorker720pHQ(transferManager,TEMP_FILE,duration,objectKeyName);
            w720MQ = getTranscodinWorker720pMQ(transferManager,TEMP_FILE,duration,objectKeyName);
            w360 = getTranscodinWorker360p(transferManager,TEMP_FILE,duration,objectKeyName);

            MediaIngestionHelper.runWorkers(w1080, w720HQ, w720MQ, w360);

            log.info("All transcoding completed");

            Files.deleteIfExists(Paths.get(TEMP_FILE));
            log.info("Transcoding completed for {}", objectKeyName);
        }
    }
}