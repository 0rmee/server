package com.ormee.server.home.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuizDto {
    private Long id;
    private String lectureTitle;
    private String quizTitle;
    private String quizDueTime;
}
