package com.ormee.server.lecture.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class StudentDetailDto {
    private Long id;
    private String name;
    private LocalDate enrolDate;
    private LocalDate blockDate;
    private String description;
}
