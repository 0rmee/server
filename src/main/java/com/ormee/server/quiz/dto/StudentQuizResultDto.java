package com.ormee.server.quiz.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudentQuizResultDto {
    private Integer correct;
    private List<ProblemDto> problemDtos;
}
