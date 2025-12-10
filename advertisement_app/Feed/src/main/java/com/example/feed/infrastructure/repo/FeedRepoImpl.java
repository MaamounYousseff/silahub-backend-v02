package com.example.feed.infrastructure.repo;

import com.example.feed.domain.model.FeedPost;
import com.example.feed.domain.repo.FeedRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FeedRepoImpl implements FeedRepo
{
    @Autowired
    private  MongoTemplate mongoTemplate;

    public FeedRepoImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Save a FeedPost into MongoDB.
     * If the postId already exists, it will be updated.
     */
    public FeedPost save(FeedPost feedPost) {
        return mongoTemplate.save(feedPost, "feeds");
    }
}
