package com.ormee.server.question.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AnswerSaveDto {
    private String content;
    private List<Long> fileIds;
}
