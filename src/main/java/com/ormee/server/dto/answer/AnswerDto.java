package com.ormee.server.dto.answer;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AnswerDto {
    private String teacherName;
    private String content;
    private String createdAt;
    private List<String> filePaths;
}
