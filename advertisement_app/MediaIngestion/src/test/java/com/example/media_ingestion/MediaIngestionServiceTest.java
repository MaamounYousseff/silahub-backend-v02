package com.example.media_ingestion;

import com.example.media_ingestion.sqsevent.S3EventNotification;
import com.example.media_ingestion.sqsevent.S3EventRecord;
import com.example.post.api.MediaChunkedDto;
import com.example.post.api.PostMediaIngestionPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFprobe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Commit;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileDownload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.*;

import static com.example.media_ingestion.MediaIngestionHelper.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j

@SpringBootTest(classes = {MediaIngestionConfig.class,
        com.example.shared.SharedConfig.class,
        com.example.post.PostConfig.class,
        com.example.shared_module_test.TestUserContext.class,
        com.example.interaction.InteractionConfig.class
})
@ComponentScan(basePackages = "com.example.transcodin")
public class MediaIngestionServiceTest {

    private static final String TEMP_FILE = "C:\\temp\\temp_video_processing.mp4";

    @Autowired
    private S3TransferManager transferManager;

    @Autowired
    @Qualifier("awsSqsObjectMapper")
    private ObjectMapper objectMapper;

    @Autowired
    private PostMediaIngestionPort postMediaIngestionPort;

    @BeforeEach
    void setUp() throws IOException {
        // Create temp directory if it doesn't exist
        Path tempDir = Paths.get("C:\\temp");
        if (!Files.exists(tempDir)) {
            Files.createDirectories(tempDir);
        }
    }

    @Test
    void testDownloadVideo() throws Exception {
        // Given
        Message message = Message.builder()
                .messageId("f576fa69-a9bb-4a15-b615-45ac91612dd6")
                .receiptHandle("AQEB4AEJz3VEbV21GXgEDaxRMXAN0pMLp5DIAL0R3FNTZamAcRGKvwVcQDK3b9Et/5yGtRX7uiibFijSxmv5F3iw034dQibYZIzB3lEvRMdvsVERH3PJi6/za38/L+aXvwrbGoMhG2hQCPKMCgdjwS8lbEjxCkpJ+ACG4BDVX5Mm097D0WzwEl4GDvQdQVL/RIUuXuhdHhi92aMzeyfX0KjjNQIBtCjbl3pKB3uVOdK2n3shERPz8zMrakTseb2+/SpojXZElYHZarlVeXxxClyl44m7IUvs30G5ttMN5UkM21JfrJklimcallsKB5WW/1C+RpIeQhs1TqC4jMNEO1V1CDO43opIZj7a710qZIAyPwAhPEfQgZu7zAHbV7pg9lIQ4VK6D72V4qB61Nm/vjvnAQ==")
                .md5OfBody("604b25c01f9bcfbb619280e1cf00168a")
                .body("{\"Records\":[{\"eventVersion\":\"2.1\",\"eventSource\":\"aws:s3\",\"awsRegion\":\"eu-north-1\",\"eventTime\":\"2026-01-19T11:42:35.584Z\",\"eventName\":\"ObjectCreated:CompleteMultipartUpload\",\"userIdentity\":{\"principalId\":\"A300LY16UFR7QM\"},\"requestParameters\":{\"sourceIPAddress\":\"94.187.14.205\"},\"responseElements\":{\"x-amz-request-id\":\"KYVTFADZP5KWEPTX\",\"x-amz-id-2\":\"hKAmr3hM0PcxP0af2KKVbK56mr4JH/UokZTk0NdSg1vzG/5pWHNEE0uAH7PSNhpvBfr25pUC89mHPrjmNiwLQ6DIejZ7K7U7EOT36i5Cugs=\"},\"s3\":{\"s3SchemaVersion\":\"1.0\",\"configurationId\":\"video_uploaded\",\"bucket\":{\"name\":\"amzn-s3-bucket-lb-01\",\"ownerIdentity\":{\"principalId\":\"A300LY16UFR7QM\"},\"arn\":\"arn:aws:s3:::amzn-s3-bucket-lb-01\"},\"object\":{\"key\":\"posts/raw/e84988ad-a4af-499f-bae5-e51d568491da_v.mp4\",\"size\":145851922,\"eTag\":\"4d7c4916826a7667357fc1d06b3f7fe3-9\",\"sequencer\":\"00696E17D182CC19F2\"}}}]}")
                .build();

        // When
        S3EventNotification eventNotification = objectMapper.readValue(message.body(), S3EventNotification.class);

        // Then - Verify event notification was parsed correctly
        assertNotNull(eventNotification);
        assertNotNull(eventNotification.getRecords());
        assertEquals(1, eventNotification.getRecords().size());

        for (S3EventRecord record : eventNotification.getRecords()) {
            String bucketName = record.getS3().getBucket().getName();
            String objectKey = record.getS3().getObject().getKey();

            log.info("Processing S3 object: bucket={}, key={}", bucketName, objectKey);


            // Download the file
            log.info("Downloading from S3: {}/{}", bucketName, objectKey);
            CompletedFileDownload downloadResult = MediaIngestionHelper.downloadFromS3(bucketName, objectKey, TEMP_FILE, transferManager);
            log.info("Download completed: {}", TEMP_FILE);

            // Verify download was successful
            assertNotNull(downloadResult);
            assertTrue(Files.exists(Paths.get(TEMP_FILE)), "Downloaded file should exist");
            assertTrue(Files.size(Paths.get(TEMP_FILE)) > 0, "Downloaded file should not be empty");

            log.info("File size: {} bytes", Files.size(Paths.get(TEMP_FILE)));
        }
    }


    @Test
//    @Commit
    void testChunkingAndUpload() throws IOException, ExecutionException, InterruptedException {
         MediaIngestionWorker w1080 ;
         MediaIngestionWorker w720HQ;
         MediaIngestionWorker w720MQ;
         MediaIngestionWorker w360;

//      Given + video should be stored in temp file with the same name
        Path videoPath = Paths.get(TEMP_FILE);
        assertTrue(Files.exists(videoPath), "Video file must exist before checking duration");
        String objectKey = "posts/raw/3be8d5ef-aa58-4dad-aaa1-186256d42441_v.mp4";

//        When
        String[] pathParts = objectKey.split("/");
        String fileNameWithExt = pathParts[pathParts.length - 1];
        String[] nameParts = fileNameWithExt.split("\\.");
        String objectKeyName = nameParts[0];

        log.info("Probing video duration: {}", TEMP_FILE);
        Double duration = getVideoDuration(TEMP_FILE, new FFprobe("ffprobe"));
        w1080 = getTranscodinWorker1080p(transferManager,TEMP_FILE,duration,objectKeyName);
        w720HQ = getTranscodinWorker720pHQ(transferManager,TEMP_FILE,duration,objectKeyName);
        w720MQ = getTranscodinWorker720pMQ(transferManager,TEMP_FILE,duration,objectKeyName);
        w360 = getTranscodinWorker360p(transferManager,TEMP_FILE,duration,objectKeyName);

//      Then
        MediaIngestionHelper.runWorkers(w1080, w720HQ, w720MQ, w360);
        log.info("All transcoding completed");

        Files.deleteIfExists(Paths.get(TEMP_FILE));
        log.info("Transcoding completed {}");

        String objectKeyPrefix = getObjectKeyPrefix(objectKey);
        String objectKeySuffix = getObjectKeySuffix(objectKey);
        MediaChunkedDto mediaChunkedDto = new MediaChunkedDto();
        mediaChunkedDto.setObjectKeyPrefix(objectKeyPrefix);
        mediaChunkedDto.setObjectKeySuffix(objectKeySuffix);
        postMediaIngestionPort.onVideoChunked(mediaChunkedDto);
    }

    private String getObjectKeyPrefix(String objectKey)
    {
        String[] str = objectKey.split("\\.");
        var objectKeyPrefix = str[0];
        return objectKeyPrefix;
    }

    private String getObjectKeySuffix(String objectKey)
    {
        String[] str = objectKey.split("\\.");
        var objectKeySuffix = str[1];
        return objectKeySuffix;
    }
}