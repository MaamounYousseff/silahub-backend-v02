package com.example.interaction.domain.repo;

import com.example.interaction.domain.model.PostUpvote;
import com.example.shared.domain.event.interaction.ToggleUpvoteState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostUpvoteRepo extends JpaRepository<PostUpvote, UUID>
{
    @Query(value = """
        INSERT INTO post_upvotes (promoter_id, post_interaction_id, upvoted_at, upvoted)
        VALUES (:promoterId, :postInteractionId, NOW(), TRUE)
        ON CONFLICT (promoter_id, post_interaction_id)
        DO UPDATE
        SET upvoted = NOT post_upvotes.upvoted,
            upvoted_at = NOW()
        RETURNING
            CASE
                WHEN xmax = 0 THEN 'INSERT'
                WHEN NOT post_upvotes.upvoted THEN 'UPDATE_REMOVED_UPVOTE'
                ELSE 'UPDATE_ADDED_UPVOTE'
            END AS action
        """, nativeQuery = true)
    ToggleUpvoteState toggleUpvote(UUID promoterId, UUID postInteractionId);

}
