package com.ormee.server.dto.quiz;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudentQuizResultDto {
    private Integer correct;
    private List<ProblemDto> problemDtos;
}
