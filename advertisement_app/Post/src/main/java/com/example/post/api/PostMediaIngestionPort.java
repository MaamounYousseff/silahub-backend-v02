package com.example.post.api;

public interface PostMediaIngestionPort
{
    boolean onVideoChunked(MediaChunkedDto mediaChunkedDto);
}
