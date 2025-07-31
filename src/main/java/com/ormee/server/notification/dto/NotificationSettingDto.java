package com.ormee.server.notification.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationSettingDto {
    private Boolean quizRegister;
    private Boolean quizRemind;
    private Boolean quizDeadline;
    private Boolean homeworkRegister;
    private Boolean homeworkRemind;
    private Boolean homeworkDeadline;
    private Boolean memo;
    private Boolean question;
    private Boolean notice;
    private Boolean event;
}

