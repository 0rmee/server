package com.ormee.server.lecture.dto;

import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.quiz.dto.QuizListDto;
import com.ormee.server.lecture.domain.LectureDay;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class LectureResponseDto {
    private Long id;
    private String profileImage;
    private String name;
    private List<String> collaborators;
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
