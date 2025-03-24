package com.ormee.server.dto.assignment;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FeedbackedAssignmentListDto {
    private List<AssignmentDto> feedbackCompletedAssignments;
    private List<AssignmentDto> feedbackNotCompletedAssignments;
}
