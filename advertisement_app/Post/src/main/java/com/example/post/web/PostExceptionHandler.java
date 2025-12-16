package com.example.post.web;

import com.example.shared.useradmin.CreatorNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class PostExceptionHandler
{
    @ExceptionHandler(CreatorNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    void handle() {
      log.error("post creator not found");
    }
}
