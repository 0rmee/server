package com.ormee.server.dto.notice;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeSaveDto {
    private String title;
    private String description;
    private LocalDateTime postDate;
    private Boolean isPinned;
    private Long likes;
}
