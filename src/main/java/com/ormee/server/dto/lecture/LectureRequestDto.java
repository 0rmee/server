package com.ormee.server.dto.lecture;

import com.ormee.server.model.LectureDay;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class LectureRequestDto {
    private String title;
//    private String description;
//    private List<LectureDay> lectureDays;
//    private LocalTime startTime;
//    private LocalTime endTime;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
}
