package com.example.feed.infrastructure.repo;

import com.example.feed.domain.model.FeedPost;
import com.example.feed.domain.repo.FeedRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

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

    public Optional<FeedPost> findByPostId(UUID postId) {
        Query query = new Query(Criteria.where("postId").is(postId));
        return Optional.ofNullable(
                mongoTemplate.findOne(query, FeedPost.class)
        );
    }
}
