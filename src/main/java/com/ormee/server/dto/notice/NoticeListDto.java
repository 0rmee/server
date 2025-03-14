package com.ormee.server.dto.notice;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeListDto {
    private String title;
    private LocalDateTime postDate;
    private Boolean isPinned;
}
