package com.ormee.server.dto.notice;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.ErrorResponse;

import java.time.LocalDateTime;

@Data
@Builder
public class NoticeDto {
    private String title;
    private String description;
    private LocalDateTime postDate;
    private Boolean isPinned;
    private Long likes;
}
