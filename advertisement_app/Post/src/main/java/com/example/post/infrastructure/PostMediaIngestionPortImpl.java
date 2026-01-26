package com.example.post.infrastructure;

import com.example.post.api.MediaChunkedDto;
import com.example.post.api.PostMediaIngestionPort;
import com.example.post.domain.Post;
import com.example.post.logic.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class PostMediaIngestionPortImpl implements PostMediaIngestionPort
{
    @Autowired
    private PostService postService;
//    TODO bas eje e3mel test lahe lahala
    @Override
    public boolean onVideoChunked(MediaChunkedDto mediaChunkedDto) {
        try {
            Post post = new Post();
//this line should change
            post.setObjectS3KeyPrefix(mediaChunkedDto.getObjectKeyPrefix());
            post.setObjectS3KeySuffix(mediaChunkedDto.getObjectKeySuffix());
            post.setS3VideoUri(mediaChunkedDto.getMasterHlsIndexUri());

            this.postService.postUploadCompleted(post);
            return true;
        }catch (RuntimeException e)
        {
            return false;
        }

    }
}
