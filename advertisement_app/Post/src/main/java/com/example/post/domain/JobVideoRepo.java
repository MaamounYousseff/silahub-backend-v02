package com.example.post.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobVideoRepo  extends JpaRepository<JobVideo, UUID> {
}
