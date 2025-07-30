package com.ormee.server.notification.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationSettingDto {
    private boolean quizRegister;
    private boolean quizRemind;
    private boolean quizDeadline;
    private boolean homeworkRegister;
    private boolean homeworkRemind;
    private boolean homeworkDeadline;
    private boolean memo;
    private boolean question;
    private boolean notice;
}

