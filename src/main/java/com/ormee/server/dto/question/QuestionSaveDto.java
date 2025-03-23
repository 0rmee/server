package com.ormee.server.dto.question;

import lombok.Data;

@Data
public class QuestionSaveDto {
    private String title;
    private String author;
    private String content;
    private Boolean isAnswered;
}
