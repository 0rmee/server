package com.ormee.server.lecture.dto;

import com.ormee.server.member.dto.AuthorDto;
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
    private String name;
    private String profileImage;
    private List<AuthorDto> coTeachers;
    private String coTeacher;
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
    private boolean isOwner;
}
