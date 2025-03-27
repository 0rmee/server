package com.ormee.server.dto.assignment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssignmentSubmitDto {
    String name;
    String content;
    String createdAt;
}
