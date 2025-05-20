package com.ormee.server.dto.assignment;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AssignmentListDto {
    List<AssignmentDto> openedAssignments;
    List<AssignmentDto> closedAssignments;
}
