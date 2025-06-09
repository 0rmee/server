package com.ormee.server.dto.home;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LectureDto {
    private String title;
    private String description;
    private Integer code;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
}
