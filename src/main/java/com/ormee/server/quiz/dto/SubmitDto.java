package com.ormee.server.quiz.dto;

import lombok.Data;

@Data
public class SubmitDto {
    private Long problemId;
    private String content;
}
