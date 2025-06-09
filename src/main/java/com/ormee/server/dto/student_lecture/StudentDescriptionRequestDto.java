package com.ormee.server.dto.student_lecture;

import lombok.Data;

@Data
public class StudentDescriptionRequestDto {
    private Long studentLectureId;
    private String description;
}
