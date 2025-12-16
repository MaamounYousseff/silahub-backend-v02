package com.example.feed.infrastructure.repo;

import com.example.feed.domain.repo.UserFeedPostHistoryRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//NOT TESTED
@Repository public class UserFeedPostHistoryRepoImpl implements UserFeedPostHistoryRepo
{

    @Autowired
    @Qualifier("feedRedisTemplate")
    private  RedisTemplate<String, Object> redisTemplate;
    private static final String CACHE_KEY = "feed:history:"; // Redis key prefix
    private final ObjectMapper objectMapper = new ObjectMapper();


    /**
     * Add a new user with a list of posts seen.
     */
    public void saveHistory(UUID explorerId, List<UUID> postIds) {
        String key = CACHE_KEY + explorerId.toString();
        List<String> postIdStrings = postIds.stream().map(UUID::toString).toList();

        // Convert list to JSON
        String json = null;
        try {
            json = new ObjectMapper().writeValueAsString(postIdStrings);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.opsForValue().set(key, json);
    }


    public List<UUID> getHistory(UUID explorerId) {
        String key = CACHE_KEY + explorerId;
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return List.of(); // return empty list if nothing found
        }

        String json = value.toString();
        try {
            List<String> postIdStrings = objectMapper.readValue(json, List.class);
            return postIdStrings.stream().map(UUID::fromString).toList();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse posts JSON from Redis", e);
        }
    }

    public void updateHistory(UUID userId, List<UUID> postIds) {
        String key = CACHE_KEY + userId;
        List<String> postIdStrings = postIds.stream().map(UUID::toString).toList();
        try {
            String json = objectMapper.writeValueAsString(postIdStrings);
            redisTemplate.opsForValue().set(key, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize posts to JSON", e);
        }
    }

    public void addPostToHistory(UUID userId, UUID postId) {
        List<UUID> posts = getHistory(userId); // reuse JSON deserialization
        if (!posts.contains(postId)) {
            List<UUID> updatedFeedPosts = new ArrayList<>(posts);
            updatedFeedPosts.add(postId);
            updateHistory(userId, updatedFeedPosts); // reuse JSON serialization
        }
    }

}
