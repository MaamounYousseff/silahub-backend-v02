package com.example.feed.infrastructure.repo;

import com.example.feed.domain.model.FeedPost;
import com.example.feed.domain.repo.FeedRepo;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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

    public boolean addLike(UUID postId, UUID explorerId) {
        Query query = Query.query(
                Criteria.where("_id").is(postId)
                        .and("likeBy").ne(explorerId) // Only update if user hasn't liked yet
        );

        Update update = new Update()
                .inc("tempTotalLike", 1)
                .addToSet("likeBy", explorerId); // addToSet prevents duplicates

        UpdateResult result = mongoTemplate.updateFirst(query, update, "feeds");

        return result.getModifiedCount() > 0;
    }

    /**
     * -1 tempTotalLike ONLY if post is boosted
     * $pull is an atomic, in-place operation : so no performance issue
     */
    public boolean removeLike(UUID postId, UUID explorerId) {
        Query query = Query.query(
                Criteria.where("_id").is(postId)
                        .and("likeBy").is(explorerId) // Only update if user has liked
        );

        Update update = new Update()
                .inc("tempTotalLike", -1)
                .pull("likeBy", explorerId); // Remove user from array

        UpdateResult result = mongoTemplate.updateFirst(query, update, "feeds");

        return result.getModifiedCount() > 0;
    }
}
