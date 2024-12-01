package com.ormee.server.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuizSaveDto {
    private String title;
    private String description;
    private LocalDateTime openTime;
    private LocalDateTime dueTime;
    private Long timeLimit;
    private Boolean isDraft;
    private List<ProblemDto> problems;
}
