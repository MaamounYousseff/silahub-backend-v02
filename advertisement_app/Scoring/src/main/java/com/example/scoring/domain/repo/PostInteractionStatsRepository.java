package com.example.scoring.domain.repo;


import com.example.scoring.domain.model.PostInteractionStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PostInteractionStatsRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Increment scoreUpdateCount by 1 for the given postId
     */
    public void incrementScoreUpdateCount(UUID postId) {
        Query query = new Query(Criteria.where("_id").is(postId));
        Update update = new Update().inc("scoreUpdateCount", 1);
        mongoTemplate.updateFirst(query, update, PostInteractionStats.class);
    }

    /**
     * Increment tempTotalWatchTime by the given amount for the given postId
     */
    public void incrementTempTotalWatchTime(UUID postId, long watchTimeSeconde) {
        Query query = new Query(Criteria.where("_id").is(postId));
        Update update = new Update().inc("tempTotalWatchTime", watchTimeSeconde);
        mongoTemplate.updateFirst(query, update, PostInteractionStats.class);
    }

    public void incrementTempTotalClick(UUID postId) {
        Query query = new Query(Criteria.where("_id").is(postId));
        Update update = new Update().inc("tempTotalClick", 1);
        mongoTemplate.updateFirst(query, update, PostInteractionStats.class);
    }

    public void incrementTempTotalLike(UUID postId) {
        Query query = new Query(Criteria.where("_id").is(postId));
        Update update = new Update().inc("tempTotalLike", 1);
        mongoTemplate.updateFirst(query, update, PostInteractionStats.class);
    }

    public void decrementTempTotalLike(UUID postId) {
        Query query = new Query(Criteria.where("_id").is(postId));
        Update update = new Update().inc("tempTotalLike", -1);
        mongoTemplate.updateFirst(query, update, PostInteractionStats.class);
    }



    /**
     * Get the PostInteractionStats for a given postId
     */
    public Optional<PostInteractionStats> findByPostId(UUID postId) {
        Query query = new Query(Criteria.where("postId").is(postId));
        PostInteractionStats stats = mongoTemplate.findOne(query, PostInteractionStats.class);
        return Optional.ofNullable(stats);
    }

    /**
     * Save PostInteractionStats into MongoDB.
     */
    public PostInteractionStats save(PostInteractionStats stats) {
        return mongoTemplate.save(stats, "post_interaction_stats");
    }

}
