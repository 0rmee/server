package com.ormee.server.quiz.dto;

import com.ormee.server.member.dto.AuthorDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class QuizDetailDto {
    private AuthorDto author;
    private String title;
    private String description;
    private boolean isOpened;
    private LocalDateTime openTime;
    private LocalDateTime dueTime;
    private Long timeLimit;
    private List<ProblemDto> problems;
}
