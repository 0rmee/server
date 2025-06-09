package com.ormee.server.dto.assignment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AssignmentDto {
    private Long id;
    private String title;
    private String description;
    private List<String> filePaths;
    private LocalDateTime openTime;
    private LocalDateTime dueTime;
}
