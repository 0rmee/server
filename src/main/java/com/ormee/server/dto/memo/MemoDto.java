package com.ormee.server.dto.memo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MemoDto {
    private String title;
    private String description;
    private LocalDateTime dueTime;
    private Boolean isOpen;
    private Integer submitCount;
}
