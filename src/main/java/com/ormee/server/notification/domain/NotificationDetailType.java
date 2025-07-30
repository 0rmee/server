package com.ormee.server.notification.domain;

import lombok.Getter;

@Getter
public enum NotificationDetailType {
    REGISTER,
    REMIND,
    DEADLINE,
    NONE,
}
