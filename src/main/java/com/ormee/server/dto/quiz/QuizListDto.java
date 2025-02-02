package com.ormee.server.dto.quiz;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuizListDto {
    private String id;
    private String quizName;
    private String quizDate;
    private Long timeLimit;
    private Boolean quizAvailable;
    private Long submitCount;
}
