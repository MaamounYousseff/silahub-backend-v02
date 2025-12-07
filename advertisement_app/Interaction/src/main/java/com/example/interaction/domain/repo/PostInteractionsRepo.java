package com.example.interaction.domain.repo;

import com.example.interaction.domain.model.PostInteractions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostInteractionsRepo extends JpaRepository<PostInteractions, UUID>
{
}
