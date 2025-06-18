package com.ormee.server.dto.feedback;

import com.ormee.server.model.StampType;
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
