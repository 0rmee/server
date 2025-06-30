package com.ormee.server.homework.dto;

import com.ormee.server.homework.domain.StampType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedbackDto {
    private Long id;
    private StampType stamp;
    private String content;
    private String createdAt;
}
