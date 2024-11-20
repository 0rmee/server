package com.ormee.server.dto;

import lombok.Data;

@Data
public class QuizDto {
    private String id;
    private String quizName;
    private String quizDate;
    private Boolean quizAvailable;
}
