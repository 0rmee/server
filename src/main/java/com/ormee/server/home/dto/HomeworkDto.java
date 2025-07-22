package com.ormee.server.home.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeworkDto {
    private Long id;
    private String lectureTitle;
    private String homeworkTitle;
    private String homeworkDueTime;
}
