package com.ormee.server.dto.home;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ListDto {
    private Long id;
    private String type;
    private String title;
    private Long submitStudents;
    private Long totalStudents;
    private LocalDateTime openTime;
    private LocalDateTime dueTime;
}
