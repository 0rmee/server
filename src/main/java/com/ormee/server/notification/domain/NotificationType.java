package com.ormee.server.notification.domain;

import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum NotificationType {
    QUIZ("퀴즈"),
    MEMO("쪽지"),
    HOMEWORK("숙제"),
    QUESTION("질문"),
    NOTICE("공지"),
    ORMEE("오르미");

    private final String korean;

    NotificationType(String korean) {
        this.korean = korean;
    }

    public static NotificationType fromKorean(String korean) {
        return Arrays.stream(NotificationType.values())
                .filter(type -> type.getKorean().equals(korean))
                .findFirst()
                .orElseThrow(() -> new CustomException(ExceptionType.NOTIFICATION_NOT_FOUND_EXCEPTION));
    }
}
