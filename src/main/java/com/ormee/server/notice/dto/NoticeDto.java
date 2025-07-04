package com.ormee.server.notice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class NoticeDto {
    private String title;
    private String description;
    private List<String> fileNames;
    private List<String> filePaths;
    private LocalDateTime postDate;
    private Boolean isPinned;
    private Boolean isLiked;
    private int likes;
}
