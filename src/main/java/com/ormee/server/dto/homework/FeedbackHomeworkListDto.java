package com.ormee.server.dto.homework;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FeedbackHomeworkListDto {
    private List<HomeworkDto> feedbackCompletedHomeworks;
    private List<HomeworkDto> feedbackNotCompletedHomeworks;
}
