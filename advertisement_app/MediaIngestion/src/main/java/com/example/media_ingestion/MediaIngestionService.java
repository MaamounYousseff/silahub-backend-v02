package com.example.media_ingestion;

import com.example.media_ingestion.sqsevent.S3EventNotification;
import com.example.media_ingestion.sqsevent.S3EventRecord;
import com.example.post.api.MediaChunkedDto;
import com.example.post.api.PostMediaIngestionPort;
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
import software.amazon.awssdk.transfer.s3.model.CompletedFileUpload;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.*;

import static com.example.media_ingestion.MediaIngestionHelper.*;

@Slf4j
@Service
public class MediaIngestionService {

//    private PostMediaIngestionPort mediaIngestion;

    public static final String S3_DELIMITER = "/";
    public static final String S3_BUCKET_NAME = "amzn-s3-bucket-lb-01";
    public static final String SQS_QUEUE_URL = "https://sqs.eu-north-1.amazonaws.com/418962810364/transcoding-queue";
    public static final String S3_OUTPUT_PATH_1080P_HQ = "posts/hls/<file_name>/1080p/";
    public static final String S3_OUTPUT_PATH_720P_HQ = "posts/hls/<file_name>/720p/";
    public static final String S3_OUTPUT_PATH_360P_LQ = "posts/hls/<file_name>/360p/";
    public static final String S3_OUTPUT_PATH_MASTER_INDEX = "posts/hls/<file_name>/master.m3u8";
    public static final String AWS_REGION = "eu-north-1";
    public static final String TEMP_SEGMENT_DIR = "C:\\temp\\video_segments";
    public static final String TEMP_WORKER_OUTPUT_DIRECTORY = "C:\\temp\\video_segments\\hls_<Thread_ID>";
    public static final String TEMP_SEGMENT_PATTERN = "C:\\temp\\video_segments\\hls_<Thread_ID>\\seg_%03d.ts";
    public static final String TEMP_INDEX_PLAYLIST_PATH = "C:\\temp\\video_segments\\hls_<Thread_ID>\\index.m3u8";

    public static final String TEMP_FILE = "C:\\temp\\temp_video_processing.mp4";
    public static final String ACCESS_KEY = "AKIAWDDBBEX6NR6WXJRW";
    public static final String SECRET_KEY = "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";
    public static final int SEGMENT_DURATION = 5;

    private final ObjectMapper objectMapper;
    private S3TransferManager transferManager;
    private SqsClient sqsClient;
    private FFmpeg ffmpeg;
    private FFprobe ffprobe;
    private CountDownLatch countDownLatch;
    private PostMediaIngestionPort postMediaIngestionPort;

    private String fileName;
    private double duration;
    private String objectKey;

    private MediaIngestionWorker w1080;
    private MediaIngestionWorker w720HQ;
    private MediaIngestionWorker w360;

    @Autowired
    public MediaIngestionService(@Qualifier("awsSqsObjectMapper") ObjectMapper objectMapper,
                                 S3TransferManager s3TransferManager,
                                 PostMediaIngestionPort postMediaIngestionPort) {
        this.objectMapper = objectMapper;
        this.transferManager = s3TransferManager;
        this.postMediaIngestionPort = postMediaIngestionPort;
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

        Files.createDirectories(Paths.get(TEMP_SEGMENT_DIR));
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
            String eventName = record.getEventName();
            if (eventName == null && !eventName.startsWith("ObjectCreated"))
            {
                log.error("Invalid Event Name");
                return;
            }

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
            w360 = getTranscodinWorker360p(transferManager,TEMP_FILE,duration,objectKeyName);

            MediaIngestionHelper.runWorkers(w1080, w720HQ,  w360);

            createAndUploadMaster(S3_BUCKET_NAME, getVideoId(objectKey));

            log.info("All transcoding completed");

            Files.deleteIfExists(Paths.get(TEMP_FILE));
            log.info("Transcoding completed for {}", objectKeyName);

            String objectKeyPrefix = getObjectKeyPrefix(objectKey);
            String objectKeySuffix = getObjectKeySuffix(objectKey);
            MediaChunkedDto mediaChunkedDto = new MediaChunkedDto();
            mediaChunkedDto.setObjectKeyPrefix(objectKeyPrefix);
            mediaChunkedDto.setObjectKeySuffix(objectKeySuffix);
            postMediaIngestionPort.onVideoChunked(mediaChunkedDto);
        }
    }


    private String getObjectKeyPrefix(String objectKey) {
        String[] parts = objectKey.split("\\.");
        if (parts.length <= 1) {
            return objectKey; // no dot, return as-is
        }

        // Join all parts except the last
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (i > 0) sb.append(".");
            sb.append(parts[i]);
        }
        return sb.toString();
    }

    private String getObjectKeySuffix(String objectKey)
    {
        String[] str = objectKey.split("\\.");
        var objectKeySuffix = str[str.length-1];
        return objectKeySuffix;
    }

// masterPath :
    private void createAndUploadMaster(String bucketName ,String objectKey ) throws Exception {
        // s3BasePath is the folder on S3 where master.m3u8 and variant playlists reside
        Path masterFilePath = Paths.get(TEMP_SEGMENT_DIR, "master.m3u8");


        try (BufferedWriter writer = Files.newBufferedWriter(masterFilePath, StandardCharsets.UTF_8)) {
            writer.write("#EXTM3U\n");
            writer.write("#EXT-X-VERSION:3\n\n");

            // 1080p
            writer.write("# 1080p Portrait\n");
            writer.write("#EXT-X-STREAM-INF:BANDWIDTH=5000000,RESOLUTION=1080x1920\n");
            writer.write("1080p/index.m3u8\n\n");

            // 720p
            writer.write("# 720p Portrait\n");
            writer.write("#EXT-X-STREAM-INF:BANDWIDTH=2800000,RESOLUTION=720x1280\n");
            writer.write("720p/index.m3u8\n\n");

            // 360p
            writer.write("# 360p Portrait\n");
            writer.write("#EXT-X-STREAM-INF:BANDWIDTH=800000,RESOLUTION=360x640\n");
            writer.write("360p/index.m3u8\n");

        } catch (IOException e) {
            throw new RuntimeException("Failed to create master.m3u8", e);
        }

        // Upload master.m3u8 to S3
        String masterS3OutputPath = S3_OUTPUT_PATH_MASTER_INDEX.replace("<file_name>", getObjectKeyName(objectKey));
        uploadToS3(S3_BUCKET_NAME, masterS3OutputPath, masterFilePath.toString());

        try {
            Files.deleteIfExists(masterFilePath);
        } catch (IOException ignored) {}

        log.info("master.m3u8 created and uploaded to S3: {}", masterS3OutputPath);
    }

    private void uploadToS3(String bucketName, String key, String filePath) throws Exception {
        log.info("Uploading to S3: bucket={}, key={}", bucketName, key);

        String contentType = resolveContentType(key);

        UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
                .putObjectRequest(req -> req
                        .bucket(bucketName)
                        .key(key)
                        .contentType(contentType)  //required , default value video type
                )
                .source(Paths.get(filePath))
                .build();

        FileUpload fileUpload = transferManager.uploadFile(uploadFileRequest);
        CompletedFileUpload uploadResult = fileUpload.completionFuture().join();

        log.info("Upload completed: {} with Content-Type={}", key, contentType);
    }

    private String resolveContentType(String key) {
        if (key.endsWith(".m3u8")) {
            return "application/vnd.apple.mpegurl";
        }
        if (key.endsWith(".ts")) {
            return "video/mp2t";
        }
        if (key.endsWith(".mp4")) {
            return "video/mp4";
        }
        return "application/octet-stream";
    }

    private String getVideoId(String objectKey)
    {
        String []str = objectKey.split("/");
        return str[str.length-1];
    }


}