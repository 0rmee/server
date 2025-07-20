package com.ormee.server.notification.dto;

import com.ormee.server.notification.domain.NotificationType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentNotificationRequestDto {
    private Long parentId;
    private NotificationType type;
    private String header;
    private String title;
    private String body;
    private String content;
}
