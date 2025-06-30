package com.ormee.server.lecture.dto;

import lombok.Data;

@Data
public class StudentDescriptionRequestDto {
    private Long studentLectureId;
    private String description;
}
