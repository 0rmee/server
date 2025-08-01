package com.ormee.server.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionType {
    MEMBER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    LECTURE_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 강의입니다."),
    QUIZ_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 퀴즈입니다."),
    PROBLEM_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 문제입니다."),
    SUBMIT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 제출입니다."),
    MEMO_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND,"존재하지 않는 쪽지입니다."),
    QUESTION_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 질문입니다."),
    ANSWER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 답변입니다."),
    NOTICE_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 공지입니다."),
    HOMEWORK_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 숙제입니다."),
    FEEDBACK_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 피드백입니다."),
    STUDENT_LECTURE_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 학생-강의입니다."),
    ATTACHMENT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 첨부파일입니다."),
    NOTIFICATION_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 알림입니다."),
    LIKE_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "좋아요 한 적 없는 글입니다."),
    COLLABORATOR_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "공동작업자가 존재하지 않습니다."),
    SUBMISSION_ALREADY_EXIST_EXCEPTION(HttpStatus.CONFLICT, "이미 제출했습니다."),
    USERNAME_ALREADY_EXIST_EXCEPTION(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다."),
    EMAIL_ALREADY_EXIST_EXCEPTION(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    LIKE_ALREADY_EXIST_EXCEPTION(HttpStatus.CONFLICT, "이미 좋아요 했습니다."),
    ACCOUNT_LOCKED_EXCEPTION(HttpStatus.LOCKED, "계정이 잠겼습니다."),
    INVALID_JWT_EXCEPTION(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    S3_REQUEST_FAILED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "S3 요청에 실패했습니다."),
    COLLABORATOR_ADD_FORBIDDEN_EXCEPTION(HttpStatus.FORBIDDEN, "공동 작업자는 1명까지만 추가 가능합니다."),
    COLLABORATOR_CHANGE_FORBIDDEN_EXCEPTION(HttpStatus.FORBIDDEN, "공동 작업자 변경은 1번만 가능합니다."),
    LECTURE_MODIFY_FORBIDDEN_EXCEPTION(HttpStatus.FORBIDDEN, "이미 개강한 강의는 수정 및 삭제가 불가능합니다."),
    QUIZ_MODIFY_FORBIDDEN_EXCEPTION(HttpStatus.FORBIDDEN, "이미 공개된 퀴즈는 수정 불가능합니다."),
    ANSWER_FORBIDDEN_EXCEPTION(HttpStatus.FORBIDDEN, "이미 답변을 등록했습니다."),
    SUBMIT_FORBIDDEN_EXCEPTION(HttpStatus.FORBIDDEN, "이미 제출하였습니다."),
    PASSWORD_INVALID_EXCEPTION(HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다."),
    ACCESS_FORBIDDEN_EXCEPTION(HttpStatus.FORBIDDEN, "접근 권한 없습니다."),
    INVALID_QUIZ_FIELD_EXCEPTION(HttpStatus.BAD_REQUEST, "퀴즈 필수 항목(title/openTime/dueTime/timeLimit)이 누락되었습니다."),
    NOTICE_PIN_FAILED_EXCEPTION(HttpStatus.BAD_REQUEST, "공지는 최대 3개까지 고정 가능합니다."),
    FILTER_INVALID_EXCEPTION(HttpStatus.BAD_REQUEST, "잘못된 필터값입니다");

    private final HttpStatus httpStatus;
    private final String message;
}