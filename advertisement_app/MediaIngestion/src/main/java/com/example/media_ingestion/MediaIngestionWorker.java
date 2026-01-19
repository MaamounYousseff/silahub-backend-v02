package com.example.media_ingestion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileUpload;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static com.example.media_ingestion.MediaIngestionService.*;

@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaIngestionWorker implements Runnable {
    private String objectKeyName;
    private String fileName;
    private double duration;
    private String outputPathTemplate;
    private int height, width, crf;
    private String audioBitrate;
    private FFmpeg ffmpeg;
    private FFprobe ffprobe;
    private S3TransferManager transferManager;


    @Override
    public void run() {
        // Each worker gets its own copy of the input file
        String workerTempFile = TEMP_FILE.replace(".mp4", "_" + Thread.currentThread().getId() + ".mp4");

        try {
            // Copy original video to worker-specific temp file
            Files.copy(Paths.get(TEMP_FILE), Paths.get(workerTempFile), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            log.info("Starting transcoding: fileName={}, resolution={}x{}, crf={}", fileName, width, height, crf);

            String outputPath = outputPathTemplate.replace("<file_name>", objectKeyName);

            int totalChunks = (int) Math.ceil(duration / CHUNK_DURATION);

            for (int i = 0; i < totalChunks; i++) {
                double startTime = i * CHUNK_DURATION;
                // Unique chunk filename per thread and per chunk
                String chunkFileName =
                        String.format("chunk_%s_%04d.mp4",
                                Thread.currentThread().getName(),
                                i);
                String tempChunkFile = TEMP_CHUNK_DIR + "\\" + chunkFileName;
                String s3Key = outputPath + chunkFileName;

                log.info("Processing chunk {}/{}: startTime={}", i + 1, totalChunks, startTime);

                FFmpegBuilder builder = new FFmpegBuilder()
                        .setInput(workerTempFile)  // Use the worker-specific copy
                        .overrideOutputFiles(true)
                        .addOutput(tempChunkFile)
                        .setStartOffset((long) (startTime * 1_000_000), TimeUnit.MICROSECONDS)
                        .setDuration((long) (CHUNK_DURATION * 1_000_000), TimeUnit.MICROSECONDS)
                        .addExtraArgs("-map", "0:v:0")
                        .addExtraArgs("-map", "0:a?")
                        .addExtraArgs("-ss", String.valueOf(startTime))
                        .setVideoCodec("libx264")
                        .setPreset("medium")
                        .addExtraArgs("-crf", String.valueOf(crf))
                        .addExtraArgs("-profile:v", "high")
                        .addExtraArgs("-pix_fmt", "yuv420p")
                        .setVideoFilter("scale=" + width + ":" + height)
                        .setAudioCodec("aac")
                        .setAudioBitRate(Integer.parseInt(audioBitrate.replace("k", "")) * 1000)
                        .addExtraArgs("-force_key_frames", "expr:gte(t,n_forced*10)")
                        .setFormat("mp4")
                        .addExtraArgs("-movflags", "frag_keyframe+empty_moov")
                        .done();

                FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
                executor.createJob(builder).run();

                uploadToS3(S3_BUCKET_NAME, s3Key, tempChunkFile);
                Files.deleteIfExists(Paths.get(tempChunkFile));
                log.info("Chunk {}/{} uploaded to S3: {}", i + 1, totalChunks, s3Key);
            }

            log.info("Transcoding completed for resolution: {}x{}", width, height);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // Delete the worker-specific temp copy
            try {
                Files.deleteIfExists(Paths.get(workerTempFile));
            } catch (Exception ignored) {}
        }
    }



    private void uploadToS3(String bucketName, String key, String filePath) throws Exception {
        log.info("Uploading to S3: bucket={}, key={}", bucketName, key);

        UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
                .putObjectRequest(req -> req.bucket(bucketName).key(key))
                .source(Paths.get(filePath))
                .build();

        FileUpload fileUpload = transferManager.uploadFile(uploadFileRequest);
        CompletedFileUpload uploadResult = fileUpload.completionFuture().join();

        log.info("Upload completed: {}", key);
    }


}

