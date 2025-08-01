package com.ormee.server.homework.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeworkSubmitStudentDto {
    private Long homeworkSubmitId;
    private String studentName;
    private Boolean isSubmitted;
    private Boolean isChecked;
    private Boolean isFeedback;
    private String createdAt;
}
