package com.ormee.server.dto.assignment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentSaveDto {
    private String title;
    private String description;
    private Boolean isDraft;
    private LocalDateTime openTime;
    private LocalDateTime dueTime;
}
