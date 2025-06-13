package com.ormee.server.dto.notice;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeListDto {
    private Long id;
    private String title;
    private LocalDateTime postDate;
    private Boolean isPinned;
    private Long likes;
}
