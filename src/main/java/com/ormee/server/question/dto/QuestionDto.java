package com.ormee.server.question.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionDto {
    private Long id;
    private String title;
    private String content;
    private Boolean isAnswered;
    private String author;
    private List<String> filePaths;
    private String createdAt;
}
