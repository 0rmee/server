package com.ormee.server.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemoDto {
    private String title;
    private String description;
    private LocalDateTime dueTime;
    private Boolean isOpen;
}
