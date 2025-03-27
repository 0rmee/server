package com.ormee.server.dto.assignment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AssignmentDto {
    private String title;
    private String description;
    private LocalDateTime openTime;
    private LocalDateTime dueTime;
}
