package com.example.interaction.domain.repo;

import com.example.interaction.domain.model.PostInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}
