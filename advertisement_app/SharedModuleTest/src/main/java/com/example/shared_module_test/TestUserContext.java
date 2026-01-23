package com.example.shared_module_test;

import com.example.shared.security.CurrentUserContext;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TestUserContext implements CurrentUserContext {

    @Override
    public UUID getUserId() {
        return UUID.randomUUID();
    }

}
