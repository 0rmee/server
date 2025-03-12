package com.ormee.server.dto.student_lecture;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentDetailDto {
    private String name;
    private String description;
}
