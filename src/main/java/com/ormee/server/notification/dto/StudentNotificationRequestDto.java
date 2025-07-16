package com.ormee.server.notification.dto;

import com.ormee.server.notification.domain.NotificationType;
import lombok.Data;

@Data
public class StudentNotificationRequestDto {
    private Long memberId;
    private Long parentId;
    private NotificationType type;
    private String title;
    private String body;
}
