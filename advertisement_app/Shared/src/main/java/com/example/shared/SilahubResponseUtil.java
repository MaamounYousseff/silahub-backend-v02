package com.example.shared;


import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Map;

public  class SilahubResponseUtil{

    private SilahubResponseUtil() {
        // utility class
    }

    public static SilahubResponse success(
            Object data,
            String message,
            Map<String, Object> metadata
    ) {
        return new SilahubResponse(
                true,           // success
                data,           // data
                message,        // message
                null,           // errorCode
                null,           // errors
                metadata        // metadata
        );
    }

    public static SilahubResponse fail(
            HttpStatus errorCode,
            String message,
            List<FieldError> errors,
            Map<String, Object> metadata
    ) {
        return new SilahubResponse(
                false,          // success
                null,           // data
                message,        // message
                errorCode,      // errorCode
                errors,         // errors
                metadata        // metadata
        );
    }
}