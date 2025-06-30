package com.ormee.server.notification.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationDto {
    private Long notificationId;
    private String type;
    private String title;
    private String description;
    private Long parentId;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
