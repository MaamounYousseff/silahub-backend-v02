package com.example.feed.web;

import com.example.feed.domain.exception.FeedPostLimitExceededException;
import com.example.feed.domain.exception.FeedPostNotExistException;
import com.example.shared.SilahubResponse;
import com.example.shared.SilahubResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@ControllerAdvice
@RestControllerAdvice
public class FeedPostExceptionHandler
{

    @ExceptionHandler(FeedPostLimitExceededException.class)
    public ResponseEntity<SilahubResponse> handleFeedPostLimitExceededException(FeedPostLimitExceededException ex) {
        SilahubResponse response = SilahubResponseUtil.fail(HttpStatus.BAD_REQUEST, ex.getMessage(), null, Map.of("limitReached",true));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @ExceptionHandler(FeedPostNotExistException.class)
    public ResponseEntity<SilahubResponse> handleFeedPostNotExistException(FeedPostNotExistException ex) {
        SilahubResponse response = SilahubResponseUtil.fail(HttpStatus.BAD_REQUEST, ex.getMessage(), null, Map.of("notExist",true));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

//    For the case when the  user ask for path  that is not exist in that case :
    //    UserAdmin handle that but until now i do not implement security so it will show as INTERNAL SERVER ERROR

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SilahubResponse> handleGenericException(Exception ex) {
        SilahubResponse response = SilahubResponseUtil.fail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", null, Map.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
