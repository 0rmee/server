package com.ormee.server.notice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeListDto {
    private Long id;
    private String author;
    private String title;
    private LocalDateTime postDate;
    private Boolean isPinned;
    private int likes;
}
