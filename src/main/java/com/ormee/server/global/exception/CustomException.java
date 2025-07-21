package com.ormee.server.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ExceptionType exceptionType;
    private final String message;

    public CustomException(ExceptionType exceptionType) {
        this(exceptionType, exceptionType.getMessage());
    }

    public CustomException(ExceptionType exceptionType, String message) {
        super(message);
        this.exceptionType = exceptionType;
        this.message = message;
    }
}