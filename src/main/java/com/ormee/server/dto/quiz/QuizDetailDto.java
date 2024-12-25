package com.ormee.server.dto.quiz;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class QuizDetailDto {
    private LocalDateTime dueTime;
    private Long timeLimit;
    private List<ProblemDto> problems;
}
