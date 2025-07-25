package com.ormee.server.memo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MemoDto {
    private Long id;
    private String author;
    private String authorImage;
    private String title;
    private String submission;
    private LocalDateTime dueTime;
    private Boolean isOpen;
    private Integer submitCount;
    private Integer totalCount;
}
