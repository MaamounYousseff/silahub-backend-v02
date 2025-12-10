package com.example.scoring.domain.repo;

import com.example.scoring.domain.model.PostScoreBucket;

import java.util.List;
import java.util.UUID;

public interface BucketRepository
{

    void addPostScore(PostScoreBucket postScoreBucket);

    void addPostsScore(List<PostScoreBucket> postScoreBucketList);

    void updatePostScore(PostScoreBucket postScoreBucket);

    PostScoreBucket deletePostScore(UUID postId);

    PostScoreBucket fetchPostScore(UUID postId);

    List<PostScoreBucket> fetchTopPosts(int offset);
}
