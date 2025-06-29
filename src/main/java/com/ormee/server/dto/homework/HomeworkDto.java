package com.ormee.server.dto.homework;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class HomeworkDto {
    private Long id;
    private String title;
    private String description;
    private boolean feedbackCompleted;
    private List<String> fileNames;
    private List<String> filePaths;
    private LocalDateTime openTime;
    private LocalDateTime dueTime;
}
