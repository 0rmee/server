package com.ormee.server.question.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionSaveDto {
    private Boolean isLocked;
    private String title;
    private String content;
    private List<Long> fileIds;
}
