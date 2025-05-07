package com.ormee.server.dto.student_lecture;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class StudentDetailDto {
    private Long id;
    private String name;
    private LocalDate enrolDate;
    private String description;
}
