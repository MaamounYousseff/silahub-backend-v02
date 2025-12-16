package com.example.feed.domain.repo;

import java.util.List;
import java.util.UUID;

public interface UserFeedPostHistoryRepo
{
    void saveHistory(UUID explorerId, List<UUID> postIds);
    List<UUID> getHistory(UUID explorerId);
    void updateHistory(UUID userId, List<UUID> postIds);
    void addPostToHistory(UUID userId, UUID postId);
}
