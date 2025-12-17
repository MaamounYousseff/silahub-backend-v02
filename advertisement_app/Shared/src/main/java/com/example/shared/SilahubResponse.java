package com.example.shared;

import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Map;


@Builder
public record SilahubResponse(
        boolean success,
        Object data,
        String message,
        HttpStatus errorCode,
        List<FieldError> errors,
        Map<String, Object> metadata
) {}


