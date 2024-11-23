package com.ormee.server.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MemoListDto {
    private String title;
    private String description;
    private LocalDateTime dueTime;
    private Integer submit;
}
