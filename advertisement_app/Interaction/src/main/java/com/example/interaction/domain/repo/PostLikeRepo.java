package com.example.interaction.domain.repo;

import com.example.interaction.domain.model.PostLike;
import com.example.shared.domain.event.interaction.ToggleLikeAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostLikeRepo extends JpaRepository<PostLike, UUID>
{
    @Query(value = """
            INSERT INTO post_likes (explorer_id, post_interaction_id, liked_at, liked)
            VALUES (:explorerId, :postInteractionId, NOW(), TRUE)
            ON CONFLICT (explorer_id, post_interaction_id)
            DO UPDATE
            SET liked = NOT post_likes.liked,
                liked_at = NOW()
            RETURNING
                CASE
                    WHEN xmax = 0 THEN 'INSERT'
                    WHEN NOT post_likes.liked THEN 'UPDATE_UNLIKED'
                    ELSE 'UPDATE_LIKED'
                END AS action
            """, nativeQuery = true)
    ToggleLikeAction toggleLike(UUID explorerId, UUID postInteractionId);


}
