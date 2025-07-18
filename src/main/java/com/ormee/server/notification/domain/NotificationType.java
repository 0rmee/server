package com.ormee.server.notification.domain;

import lombok.Getter;

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

}
