package com.ormee.server.dto.assignment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssignmentSubmitStudentDto {
    private Long assignmentSubmitId;
    private String studentName;
}
