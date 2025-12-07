package com.example.interaction.domain.repo;

import com.example.interaction.domain.model.PostInteractions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostInteractionsRepo extends JpaRepository<PostInteractions, UUID>
{
}
