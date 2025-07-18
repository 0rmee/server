package com.ormee.server.quiz.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TeacherQuizListDto {
    private List<QuizListDto> openQuizzes;
    private List<QuizListDto> closedQuizzes;
}
