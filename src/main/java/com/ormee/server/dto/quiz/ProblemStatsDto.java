package com.ormee.server.dto.quiz;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ProblemStatsDto {
    private String content;
    private String type;
    private String answer;
    private List<Map<String, Object>> results;
}
