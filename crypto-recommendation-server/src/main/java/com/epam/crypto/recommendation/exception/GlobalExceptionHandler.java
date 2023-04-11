package com.epam.crypto.recommendation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({NoSuchDataException.class})
    public ResponseEntity<Object> handleException(NoSuchDataException exception) {
        return new ResponseEntity<>(Collections.singletonMap("message", exception.getMessage()),
                HttpStatus.NOT_FOUND);
    }
}
