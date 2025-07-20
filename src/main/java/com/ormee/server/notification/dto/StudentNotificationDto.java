package com.ormee.server.notification.dto;

import com.ormee.server.notification.domain.StudentNotification;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentNotificationDto {
    private Long id;
    private String type;
    private Long parentId;
    private String header;
    private String title;
    private String body;
    private String content;
    private Boolean isRead;

    public static StudentNotificationDto toDto(StudentNotification studentNotification) {
        return StudentNotificationDto.builder()
                .id(studentNotification.getId())
                .type(studentNotification.getType().getKorean())
                .parentId(studentNotification.getParentId())
                .header(studentNotification.getHeader())
                .title(studentNotification.getTitle())
                .body(studentNotification.getBody())
                .content(studentNotification.getContent())
                .isRead(studentNotification.getIsRead())
                .build();
    }
}
