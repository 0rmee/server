package com.ormee.server.dto;

import com.ormee.server.model.LectureDay;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class LectureResponseDto {
    private String id;
    private String profileImage;
    private String name;
    private String title;
    private String description;
    private List<LectureDay> lectureDays;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime dueTime;
    private List<QuizDto> quizList;
    private Integer activeQuizCount;
    private boolean messageAvailable;
}
