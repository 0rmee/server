package com.ormee.server.dto.question;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuestionListDto {
    private Long id;
    private String title;
    private Boolean isAnswered;
    private String author;
    private LocalDateTime createdAt;
}
