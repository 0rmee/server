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
    MEMO_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND,"존재하지 않는 쪽지입니다."),
    STUDENT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 학생입니다."),
    ASSIGNMENT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 과제입니다."),
    FEEDBACK_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 피드백입니다."),
    STUDENT_LECTURE_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 학생-강의입니다."),
    ASSIGNMENT_SUBMIT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 과제 제출입니다."),
    PASSWORD_INVALID_EXCEPTION(HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}