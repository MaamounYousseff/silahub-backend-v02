package com.example.scoring.infrastructure.repo;

import com.example.scoring.domain.model.PostScoreBucket;
import com.example.scoring.domain.repo.BucketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.*;

import static com.example.scoring.domain.Constant.*;

@Repository
public class BucketRepositoryImpl implements BucketRepository
{
    private  RedisTemplate<String, Object> redisTemplate;
    private  ZSetOperations<String, Object> zSetOps;

    @Autowired
    public BucketRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.zSetOps = redisTemplate.opsForZSet();
    }

    public void addPostsScore(List<PostScoreBucket> postScoreBucketList) {
        postScoreBucketList.stream().forEach(e -> addPostScore(e));
    }

    public void addPostScore(PostScoreBucket postScoreBucket) {
        String bucketKey = getBucketKey(postScoreBucket.getScore());
        double invertedScore = -postScoreBucket.getScore();
        zSetOps.add(bucketKey, postScoreBucket.getPostId().toString(), invertedScore);
    }

    public void updatePostScore(PostScoreBucket postScoreBucket) {
        String bucketKey = getBucketKey(postScoreBucket.getScore());
        // Store inverted score so high scores come first
        double invertedScore = -postScoreBucket.getScore();
        zSetOps.add(bucketKey, postScoreBucket.getPostId().toString(), invertedScore);
    }

    public PostScoreBucket deletePostScore(UUID postId) {
        String[] buckets = {POST_SCORING_BUCKET_LOW, POST_SCORING_BUCKET_MEDIUM, POST_SCORING_BUCKET_HIGH};

        for (String bucketKey : buckets) {
            Double invertedScore = zSetOps.score(bucketKey, postId.toString());
            if (invertedScore != null) {
                // Remove the post from this bucket
                zSetOps.remove(bucketKey, postId.toString());
                // Invert back to get real score
                long realScore = (long) (-invertedScore);
                return new PostScoreBucket(postId, realScore);
            }
        }
        // Not found in any bucket
        return null;
    }

    public List<PostScoreBucket> fetchTopPosts(int offset) {
        LinkedHashSet<PostScoreBucket> topPosts = new LinkedHashSet<>();
        String[] buckets = {POST_SCORING_BUCKET_LOW, POST_SCORING_BUCKET_MEDIUM, POST_SCORING_BUCKET_HIGH};

        for (String bucketKey : buckets) {
            Set<Object> result = zSetOps.range(bucketKey, offset, offset+1); // first element = highest
            if (result != null && !result.isEmpty()) {
                String postIdStr = (String) result.iterator().next();
                Double score = -zSetOps.score(bucketKey, postIdStr); // invert if stored as negative
                topPosts.add(new PostScoreBucket(UUID.fromString(postIdStr), score.longValue()));
            }
        }
        return new ArrayList<>(topPosts);
    }

    public PostScoreBucket fetchPostScore(UUID postId) {
        String[] buckets = {POST_SCORING_BUCKET_LOW, POST_SCORING_BUCKET_MEDIUM, POST_SCORING_BUCKET_HIGH};

        for(String bucket : buckets) {
            Double score = zSetOps.score(bucket, postId.toString());
            if (score != null)
                return new PostScoreBucket(postId , score.longValue());
        }
        return null;
    }

    private String getBucketKey(long score)
    {
        if(score <=  LOW_BUCKET_SCORE)
            return POST_SCORING_BUCKET_LOW;
        else if (score > LOW_BUCKET_SCORE && score <= MEDIUM_BUCKET_SCORE )
            return POST_SCORING_BUCKET_MEDIUM;

        return POST_SCORING_BUCKET_HIGH;
    }
}
