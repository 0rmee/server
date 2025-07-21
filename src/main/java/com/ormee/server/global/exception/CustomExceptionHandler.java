package com.ormee.server.global.exception;

import com.ormee.server.global.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity handleException(CustomException e) {
        return ResponseEntity.status(e.getExceptionType().getHttpStatus()).body(ResponseDto.fail(e.getExceptionType().getHttpStatus().value(), e.getMessage()));
    }
}
