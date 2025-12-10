package com.example.interaction.domain.repo;

import com.example.interaction.domain.model.PostInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public interface PostInteractionRepo extends JpaRepository<PostInteraction, UUID>
{
    @Modifying
    @Query("""
        UPDATE PostInteraction p
        SET p.totalLikes = p.totalLikes + 1
        WHERE p.postId = :postId
    """)
    void incrementLikes(UUID postId);


    @Modifying
    @Query("""
        UPDATE PostInteraction p
        SET p.totalLikes = p.totalLikes - 1
        WHERE p.postId = :postId
    """)
    void decrementLikes(UUID postId);

    @Modifying
    @Query("""
    UPDATE PostInteraction p
    SET p.totalUpvotes = p.totalUpvotes + 1,
        p.boostedAt = :boostedAt
    WHERE p.postId = :postId
""")
    void incrementUpvotesAndBoost(UUID postId, OffsetDateTime boostedAt);

    @Modifying
    @Query("""
    UPDATE PostInteraction p
    SET p.totalUpvotes = p.totalUpvotes + 1
    WHERE p.postId = :postId
""")
    void incrementUpvotes(UUID postId);


    @Modifying
    @Query("""
        UPDATE PostInteraction p
        SET p.totalUpvotes = p.totalUpvotes - 1
        WHERE p.postId = :postId
    """)
    void decrementUpvotes(UUID postId);


    @Modifying
    @Query("""
        UPDATE PostInteraction p
        SET p.totalClicks = p.totalClicks + 1
        WHERE p.postId = :postId
    """)
    void incrementClick(UUID postId);

    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE PostInteraction p
        SET p.totalWatchSeconds = p.totalWatchSeconds + :amount
        WHERE p.postId = :postId
    """)
    void incrementWatchSeconds(UUID postId, long amount);




}
