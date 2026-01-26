package com.example.media_ingestion;

import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileDownload;
import software.amazon.awssdk.transfer.s3.model.DownloadFileRequest;
import software.amazon.awssdk.transfer.s3.model.FileDownload;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.*;

import static com.example.media_ingestion.MediaIngestionService.*;

@Slf4j
public class MediaIngestionHelper
{

    public static CompletedFileDownload downloadFromS3(String bucketName, String objectKey, String localPath, S3TransferManager transferManager) throws Exception {
        DownloadFileRequest downloadFileRequest = DownloadFileRequest.builder()
                .getObjectRequest(req -> req.bucket(bucketName).key(objectKey))
                .destination(Paths.get(localPath))
                .build();

        FileDownload downloadFile = transferManager.downloadFile(downloadFileRequest);
        CompletedFileDownload downloadResult = downloadFile.completionFuture().join();
        return downloadResult;
    }

    public static double getVideoDuration(String videoPath, FFprobe ffprobe) throws IOException {
        FFmpegProbeResult probeResult = ffprobe.probe(videoPath);
        return probeResult.getFormat().duration;
    }


    public static String getObjectKeyName(String objectKey)
    {
        String[] pathParts = objectKey.split("/");
        String fileNameWithExt = pathParts[pathParts.length - 1];
        String[] nameParts = fileNameWithExt.split("\\.");
        String objectKeyName = nameParts[0];
        return objectKeyName;
    }


// change
    public static MediaIngestionWorker getTranscodinWorker1080p(S3TransferManager transferManager , String tempFileName, Double rootVideoDuration, String rootObjectKeyName) throws IOException {
        return MediaIngestionWorker.builder()
                .s3ObjectKeyName(rootObjectKeyName)
                .tempFileName(tempFileName)
                .rootVideoDuration(rootVideoDuration)
                .s3OutputPathTemplate(S3_OUTPUT_PATH_1080P_HQ)
                .height(1080)
                .width(1920)
                .crf(21)
                .audioBitrate("192k")
                .ffmpeg(new FFmpeg("ffmpeg"))
                .ffprobe(new FFprobe("ffprobe"))
                .transferManager(transferManager)
                .build();
    }


    public static MediaIngestionWorker getTranscodinWorker720pHQ(S3TransferManager transferManager , String tempFileName, Double rootVideoDuration, String rootObjectKeyName) throws IOException {
        return MediaIngestionWorker.builder()
                .s3ObjectKeyName(rootObjectKeyName)
                .tempFileName(tempFileName)
                .rootVideoDuration(rootVideoDuration)
                .s3OutputPathTemplate(S3_OUTPUT_PATH_720P_HQ)
                .height(720)
                .width(1280)
                .crf(23)
                .audioBitrate("128k")
                .ffmpeg(new FFmpeg("ffmpeg"))
                .ffprobe(new FFprobe("ffprobe"))
                .transferManager(transferManager)
                .build();
    }


    public static MediaIngestionWorker getTranscodinWorker360p(S3TransferManager transferManager , String tempFileName, Double rootVideoDuration, String rootObjectKeyName) throws IOException {
        return MediaIngestionWorker.builder()
                .s3ObjectKeyName(rootObjectKeyName)
                .tempFileName(tempFileName)
                .rootVideoDuration(rootVideoDuration)
                .s3OutputPathTemplate(S3_OUTPUT_PATH_360P_LQ)
                .height(360)
                .width(640)
                .crf(32)
                .audioBitrate("64k")
                .ffmpeg(new FFmpeg("ffmpeg"))
                .ffprobe(new FFprobe("ffprobe"))
                .transferManager(transferManager)
                .build();
    }




    public static void runWorkers(MediaIngestionWorker w1080 , MediaIngestionWorker w720HQ,  MediaIngestionWorker w360) throws ExecutionException, InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(4);

        Future<?> f1 = executor.submit(w1080);
        Future<?> f2 = executor.submit(w720HQ);
        Future<?> f4 = executor.submit(w360);

        f1.get();
        f2.get();
        f4.get();

        executor.shutdown();
    }


}
