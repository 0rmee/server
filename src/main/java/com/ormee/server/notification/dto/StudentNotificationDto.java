package com.ormee.server.notification.dto;

import com.ormee.server.notification.domain.StudentNotification;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StudentNotificationDto {
    private Long id;
    private String authorImage;
    private String type;
    private Long parentId;
    private String header;
    private String title;
    private String body;
    private String content;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public static StudentNotificationDto toDto(StudentNotification studentNotification, String authorImage) {
        return StudentNotificationDto.builder()
                .id(studentNotification.getId())
                .authorImage(authorImage)
                .type(studentNotification.getType().getKorean())
                .parentId(studentNotification.getParentId())
                .header(studentNotification.getHeader())
                .title(studentNotification.getTitle())
                .body(studentNotification.getBody())
                .content(studentNotification.getContent())
                .isRead(studentNotification.getIsRead())
                .createdAt(studentNotification.getCreatedAt())
                .build();
    }
}
