package com.ormee.server.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuizStatsDto {
    private long rank;
    private long problemNum;
    private long incorrectRate;
    private long incorrectCount;
}
