package com.ormee.server.notification.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudentNotificationListDto {
    private Long count;
    private List<StudentNotificationDto> notifications;
}
