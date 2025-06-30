package com.ormee.server.homework.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class HomeworkSaveDto {
    private String title;
    private String description;
    private List<Long> fileIds;
    private Boolean isDraft;
    private LocalDateTime openTime;
    private LocalDateTime dueTime;
}
