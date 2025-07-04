package com.ormee.server.quiz.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StudentQuizDto {
    private Long quizId;
    private String title;
    private String author;
    private LocalDateTime openTime;
    private LocalDateTime dueTime;
    private boolean isSubmitted;
}
