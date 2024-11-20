package com.ormee.server.dto;

import com.ormee.server.model.LectureDay;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class LectureDto {
    private String title;
    private String description;
    private List<LectureDay> lectureDays;
    private LocalTime lectureTime;
    private LocalDateTime dueTime;
}
