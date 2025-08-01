package com.ormee.server.question.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AnswerDto {
    private Long id;
    private String teacherName;
    private String teacherImage;
    private String content;
    private String createdAt;
    private List<Long> fileIds;
    private List<String> filePaths;
}
