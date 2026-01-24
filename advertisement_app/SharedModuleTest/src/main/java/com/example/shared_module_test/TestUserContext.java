package com.example.shared_module_test;

import com.example.shared.security.CurrentUserContext;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TestUserContext implements CurrentUserContext {

    @Override
    public UUID getUserId() {
        return UUID.fromString("cfd8a81c-7bae-4f3b-a8f2-f1e280e51b43");
    }

}
