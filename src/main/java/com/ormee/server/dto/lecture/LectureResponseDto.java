package com.ormee.server.dto.lecture;

import com.ormee.server.dto.quiz.QuizListDto;
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
    private Integer code;
    private String profileImage;
    private String name;
    private String title;
    private String description;
    private List<LectureDay> lectureDays;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private Integer students;
    private List<QuizListDto> quizList;
    private Long activeQuizCount;
    private boolean messageAvailable;
}
