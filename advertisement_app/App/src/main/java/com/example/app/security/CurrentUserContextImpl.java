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
//        TODO.. UN-COMMIT
//          return userIdHolder.get();
        return UUID.fromString("bdebadda-afa7-42e0-b9ef-12479fa370e0");
    }
}
