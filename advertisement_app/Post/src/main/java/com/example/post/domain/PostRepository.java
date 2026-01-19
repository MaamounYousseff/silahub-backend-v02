package com.example.post.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID>
{
    Optional<Post> findByObjectS3KeyPrefix(String objectKeyPrefix);

    @Transactional
    @Modifying
    @Query("""
        UPDATE Post p
        SET p.s3VideoUri = :videoUri,
            p.objectS3KeySuffix = :objectKeySuffix,
            p.status = :status
        WHERE p.id = :postId
    """)
    int update(UUID postId ,String videoUri ,String objectKeySuffix, String status);
}
