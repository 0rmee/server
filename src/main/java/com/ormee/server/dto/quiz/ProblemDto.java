package com.ormee.server.dto.quiz;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProblemDto {
    private Long id;
    private String content;
    private String type;
    private String answer;
    private List<String> items;

    private String submission;
    private Boolean isCorrect;
}
