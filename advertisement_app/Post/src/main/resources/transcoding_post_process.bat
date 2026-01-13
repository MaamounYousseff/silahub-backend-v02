@echo off
setlocal enabledelayedexpansion

REM ==========================
REM Variables
REM ==========================
set BUCKET=s3://amzn-s3-bucket-lb-01
set INPUT_PATH=post/raw/test.mp4
set OUTPUT_PREFIX=post/processed/1080p_hq/output_1080p_portrait_
set CHUNK_DURATION=10
set TEMP_FILE=%TEMP%\temp_video_processing.mp4
set TEMP_CHUNK_DIR=%TEMP%\video_chunks
set TEMP_CHUNK_FILE=%TEMP_CHUNK_DIR%\chunk.mp4
set AWS_REGION=eu-north-1

REM Create temp directory for chunks
if not exist "%TEMP_CHUNK_DIR%" mkdir "%TEMP_CHUNK_DIR%"

REM ==========================
REM Download video from S3
REM ==========================
echo Downloading video from S3...
aws s3 cp %BUCKET%/%INPUT_PATH% %TEMP_FILE% --region %AWS_REGION%

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to download video from S3
    exit /b 1
)

echo SUCCESS: Video downloaded successfully!
echo NEXT ANALYSE VIDEO DURATION
pause 

REM ==========================
REM Get total duration in seconds (using ffprobe)
REM ==========================
echo Analyzing video duration...
for /f "delims=" %%a in ('ffprobe -v error -show_entries format^=duration -of default^=noprint_wrappers^=1:nokey^=1 "%TEMP_FILE%"') do set DURATION=%%a

REM Remove decimal part
for /f "tokens=1 delims=." %%a in ("!DURATION!") do set DURATION=%%a

echo Total duration: %DURATION% seconds
echo NEXT : START CHUNKING
pause 


REM ==========================
REM Loop over chunks
REM ==========================
echo Starting chunking process...
echo.

set /a CHUNK_INDEX=0
set /a START=0

:LOOP
if %START% GEQ %DURATION% goto END

    echo Processing chunk %CHUNK_INDEX% starting at %START% s

    REM Process chunk to local file first
    ffmpeg -y -ss %START% -i "%TEMP_FILE%" -t %CHUNK_DURATION% -map 0:v:0 -map 0:a? -c:v libx264 -preset medium -crf 23 -profile:v high -pix_fmt yuv420p -vf "scale=1080:1920" -c:a aac -b:a 192k -force_key_frames "expr:gte(t,n_forced*10)" -f mp4 -movflags frag_keyframe+empty_moov "%TEMP_CHUNK_FILE%"

    if %ERRORLEVEL% NEQ 0 (
        echo ERROR: Failed to encode chunk %CHUNK_INDEX%
        goto CLEANUP
    )

    echo Chunk %CHUNK_INDEX% encoded successfully, uploading to S3...

    REM Upload chunk to S3 with retry logic
    set /a RETRY=0
    :UPLOAD_RETRY
    aws s3 cp "%TEMP_CHUNK_FILE%" %BUCKET%/%OUTPUT_PREFIX%%CHUNK_INDEX%.mp4 --region %AWS_REGION%

    if %ERRORLEVEL% EQU 0 (
        echo Chunk %CHUNK_INDEX% uploaded successfully
        echo.
        goto UPLOAD_SUCCESS
    )

    set /a RETRY=!RETRY! + 1
    if !RETRY! LSS 3 (
        echo Upload failed, retrying... ^(attempt !RETRY!/3^)
        timeout /t 3 /nobreak >NUL
        goto UPLOAD_RETRY
    )

    echo ERROR: Failed to upload chunk %CHUNK_INDEX% after 3 attempts
    goto CLEANUP

    :UPLOAD_SUCCESS
    REM Increment
    set /a START=%START% + %CHUNK_DURATION%
    set /a CHUNK_INDEX=%CHUNK_INDEX% + 1

    goto LOOP

:END
echo.
echo All chunks uploaded successfully!

:CLEANUP
echo.
echo Cleaning up temporary files...
del "%TEMP_FILE%" 2>NUL
if exist "%TEMP_CHUNK_DIR%" rd /s /q "%TEMP_CHUNK_DIR%"
echo Done!