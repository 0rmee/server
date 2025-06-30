package com.ormee.server.lecture.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class LectureRequestDto {
    private String password;
    private String title;
    private String description;
    private List<String> lectureDays;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
}
