package com.example.post.web;

import com.example.shared.SilahubResponse;
import com.example.shared.SilahubResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@ControllerAdvice
public class PostExceptionHandler
{
    @ExceptionHandler(Exception.class)
    public ResponseEntity<SilahubResponse> handleGenericException(Exception ex) {
        SilahubResponse response = SilahubResponseUtil.fail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", null, Map.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
