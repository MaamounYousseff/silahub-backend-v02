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
    @Override
    public FeedPost save(FeedPost feedPost) {
        return mongoTemplate.save(feedPost, "feeds");
    }

    @Override
    public Optional<FeedPost> findByPostId(UUID postId) {
        Query query = new Query(Criteria.where("postId").is(postId));
        return Optional.ofNullable(
                mongoTemplate.findOne(query, FeedPost.class)
        );
    }

    @Override
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
    @Override
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

    @Override
    public boolean addUpvote(UUID postId, UUID promoterId, Long boostAt) {
        Query query = Query.query(
                Criteria.where("_id").is(postId)
                        .and("upvotedBy").ne(promoterId)
        );

        Update update = new Update()
                .inc("tempTotalUpvote", 1)
                .addToSet("upvotedBy", promoterId)
                .set("boostedAt", boostAt);

        UpdateResult result = mongoTemplate.updateFirst(query, update, "feeds");

        return result.getModifiedCount() > 0;
    }

    @Override
    public boolean updateUpvote(UUID postId, UUID promoterId) {
        Query query = Query.query(
                Criteria.where("_id").is(postId)
                        .and("upvotedBy").ne(promoterId)
        );

        Update update = new Update()
                .inc("tempTotalUpvote", 1)
                .addToSet("upvotedBy", promoterId);

        UpdateResult result = mongoTemplate.updateFirst(query, update, "feeds");

        return result.getModifiedCount() > 0;
    }

    @Override
    public boolean removeUpvote(UUID postId, UUID promoterId) {
        Query query = Query.query(
                Criteria.where("_id").is(postId)
                        .and("upvotedBy").is(promoterId) // Only update if user has liked
        );

        Update update = new Update()
                .inc("tempTotalUpvote", -1)
                .pull("upvotedBy", promoterId); // Remove user from array

        UpdateResult result = mongoTemplate.updateFirst(query, update, "feeds");

        return result.getModifiedCount() > 0;
    }


    @Override
    public boolean addNewAsset(UUID postId, String assetUrl, String type) {
        try {
            // Build query to find the post by postId
            Query query = new Query(Criteria.where("postId").is(postId));

            // Prepare the update
            Update update = new Update();

            // Set the asset depending on type
            if ("image".equalsIgnoreCase(type)) {
                update.addToSet("ImageUrls", assetUrl); // add to list
            } else if ("thumbnail".equalsIgnoreCase(type)) {
                update.set("thumbnailUrl", assetUrl); // set thumbnail
            } else {
                throw new IllegalArgumentException("Unknown asset type: " + type);
            }

            // Set fields only on insert
            update.setOnInsert("postId", postId);
            update.setOnInsert("timeStamp", System.currentTimeMillis());
            update.setOnInsert("status", "pending"); // only if creating new post

            // Upsert: insert if not exists, otherwise update
            mongoTemplate.upsert(query, update, FeedPost.class);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void update(FeedPost feed) {

        Query query = new Query(Criteria.where("postId").is(feed.getPostId()));
        Update update = new Update();

        // -------- Core identity / lifecycle --------
        if (feed.getCreatorId() != null)
            update.set("creatorId", feed.getCreatorId());

        if (feed.getTimeStamp() != null)
            update.set("timeStamp", feed.getTimeStamp());

        if (feed.getStatus() != null)
            update.set("status", feed.getStatus());

        if (feed.getBoostedAt() != null)
            update.set("boostedAt", feed.getBoostedAt());

        // -------- Creator info --------
        if (feed.getCreatorLogoUrl() != null)
            update.set("creatorLogoUrl", feed.getCreatorLogoUrl());

        if (feed.getCreatorName() != null)
            update.set("creatorName", feed.getCreatorName());

        if (feed.getWhatsappNumber() != null)
            update.set("whatsappNumber", feed.getWhatsappNumber());

        if (feed.getLongitude() != null)
            update.set("longitude", feed.getLongitude());

        if (feed.getLatitude() != null)
            update.set("latitude", feed.getLatitude());

        // -------- Counters / scoring --------
        if (feed.getTempTotalLike() != null)
            update.set("tempTotalLike", feed.getTempTotalLike());

        if (feed.getTempTotalUpvote() != null)
            update.set("tempTotalUpvote", feed.getTempTotalUpvote());

        if (feed.getTempTotalClick() != null)
            update.set("tempTotalClick", feed.getTempTotalClick());

        if (feed.getTempTotalWatchTime() != null)
            update.set("tempTotalWatchTime", feed.getTempTotalWatchTime());

        if (feed.getScoreUpdateCount() != null)
            update.set("scoreUpdateCount", feed.getScoreUpdateCount());

        // -------- Engagement --------
        if (feed.getLikeBy() != null )
            update.set("likeBy", feed.getLikeBy());

        if (feed.getUpvotedBy() != null )
            update.set("upvotedBy", feed.getUpvotedBy());

        // -------- Safety guard --------
        if (update.getUpdateObject().isEmpty()) {
            return; // nothing to update
        }

        mongoTemplate.updateFirst(query, update, FeedPost.class);
    }



}
