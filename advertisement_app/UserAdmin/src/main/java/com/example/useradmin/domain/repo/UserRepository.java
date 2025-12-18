package com.example.useradmin.domain.repo;

import com.example.useradmin.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository
{
    List<User> findAll();
    User findUserByUsername(String username);
    Page<User> getUsers(Pageable pageable);
    User disableUser(UUID userId);
    User enableUser(UUID userId);
    Optional<User> findById(UUID userId);
}
