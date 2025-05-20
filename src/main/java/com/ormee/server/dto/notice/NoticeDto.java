package com.ormee.server.dto.notice;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class NoticeDto {
    private String title;
    private String description;
    private List<String> filePaths;
    private LocalDateTime postDate;
    private Boolean isPinned;
    private Long likes;
}
