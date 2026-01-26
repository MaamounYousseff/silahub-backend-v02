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
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;

import static com.example.media_ingestion.MediaIngestionService.*;

@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaIngestionWorker implements Runnable {

    private String s3ObjectKeyName;
    private String tempFileName;
    private double rootVideoDuration;
    private String s3OutputPathTemplate;
    private int height, width, crf;
    private String audioBitrate;
    private FFmpeg ffmpeg;
    private FFprobe ffprobe;
    private S3TransferManager transferManager;

    @Override
    public void run() {
        String workerTempFile = TEMP_FILE.replace(".mp4", "_" + Thread.currentThread().getId() + ".mp4");

        try {
            Files.copy(Paths.get(TEMP_FILE), Paths.get(workerTempFile), StandardCopyOption.REPLACE_EXISTING);

            log.info("Starting HLS transcoding: {} {}x{}", tempFileName, width, height);


            String tempWorkerOutputDir      = TEMP_WORKER_OUTPUT_DIRECTORY.replace("<Thread_ID>", String.valueOf(Thread.currentThread().getId()));
            String tempSegmentPattern   = TEMP_SEGMENT_PATTERN.replace("<Thread_ID>", String.valueOf(Thread.currentThread().getId()));
            String tempIndexPlaylistPath = TEMP_INDEX_PLAYLIST_PATH.replace("<Thread_ID>", String.valueOf(Thread.currentThread().getId()));

            Files.createDirectories(Paths.get(tempWorkerOutputDir));

            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(workerTempFile)// video to convert
                    .overrideOutputFiles(false)

                    .addOutput(tempIndexPlaylistPath)
                    .setFormat("hls")

                    // HLS settings
                    .addExtraArgs("-hls_time", String.valueOf(SEGMENT_DURATION))
                    .addExtraArgs("-hls_playlist_type", "vod")
                    .addExtraArgs("-hls_segment_filename", tempSegmentPattern)

                    //  Keyframe alignment  : Why important? Without this, seeking or switching segments can glitch in HLS players.
                    .addExtraArgs("-g", "48") // Keyframes are full frames that can be used to jump or seek in the video
                    .addExtraArgs("-sc_threshold", "0") // Makes scene changes ignored for keyframe placement, so segments are perfectly aligned.

                    // Video
                    .setVideoCodec("libx264")
                    .setPreset("medium")// Balance between speed and compression quality.
                    .addExtraArgs("-crf", String.valueOf(crf))// Constant quality control (lower = better quality).
                    .addExtraArgs("-profile:v", "high")
                    .addExtraArgs("-pix_fmt", "yuv420p")
                    .setVideoFilter("scale=" + width + ":" + height)

                    // Audio
                    .setAudioCodec("aac")
                    .setAudioBitRate(Integer.parseInt(audioBitrate.replace("k", "")) * 1000)
                    .done();

            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
            executor.createJob(builder).run();

            log.info("HLS packaging finished, uploading files...");

            // Upload all generated HLS files
            Files.list(Paths.get(tempWorkerOutputDir)).forEach(path -> {
                try {
                    String s3Key = s3OutputPathTemplate.replace("<file_name>",s3ObjectKeyName) + path.getFileName().toString();
                    uploadToS3(S3_BUCKET_NAME, s3Key, path.toString());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            log.info("Upload complete for resolution {}x{}", width, height);

            // Cleanup
            Files.walk(Paths.get(tempWorkerOutputDir))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                Files.deleteIfExists(Paths.get(workerTempFile));
            } catch (Exception ignored) {}
        }
    }

    private void uploadToS3(String bucketName, String key, String filePath) throws Exception {
        String contentType = resolveContentType(key);

        UploadFileRequest request = UploadFileRequest.builder()
                .putObjectRequest(req -> req
                        .bucket(bucketName)
                        .key(key)
                        .contentType(contentType))
                .source(Paths.get(filePath))
                .build();

        transferManager.uploadFile(request).completionFuture().join();
        log.info("Uploaded: {} ({})", key, contentType);
    }

    private String resolveContentType(String key) {
        if (key.endsWith(".m3u8")) return "application/vnd.apple.mpegurl";
        if (key.endsWith(".ts"))   return "video/mp2t";
        return "application/octet-stream";
    }
}

