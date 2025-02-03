package com.ormee.server.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionType {
    TEACHER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 선생님코드입니다."),
    LECTURE_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 강의입니다."),
    QUIZ_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 퀴즈입니다."),
    PROBLEM_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 문제입니다."),
    SUBMIT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 제출입니다."),
    MEMO_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND,"존재하지 않는 쪽지입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}