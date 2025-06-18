package com.ormee.server.dto.feedback;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedbackDto {
    private Long id;
    private String stamp;
    private String content;
    private String createdAt;
}
