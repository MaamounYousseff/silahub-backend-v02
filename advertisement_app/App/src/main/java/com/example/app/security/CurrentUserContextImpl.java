package com.example.app.security;

import com.example.shared.security.CurrentUserContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;


@Component
@RequestScope
public class CurrentUserContextImpl implements CurrentUserContext {

    private static final ThreadLocal<UUID> userIdHolder = new ThreadLocal<>();

    public  void setUserId(UUID userId) {
       userIdHolder.set(userId);
    }

    @Override
    public UUID getUserId() {
        UUID userId = UUID.fromString("cfd8a81c-7bae-4f3b-a8f2-f1e280e51b43");
        return userId;
    }
}
