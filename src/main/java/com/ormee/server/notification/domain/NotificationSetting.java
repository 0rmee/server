package com.ormee.server.notification.domain;

import com.ormee.server.global.config.EntityTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class NotificationSetting extends EntityTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long memberId;

    @Column
    private String deviceToken;

    // 퀴즈
    private boolean quizRegister = true;
    private boolean quizRemind = true;
    private boolean quizDeadline = true;

    // 숙제
    private boolean homeworkRegister = true;
    private boolean homeworkRemind = true;
    private boolean homeworkDeadline = true;

    // 쪽지 / 질문 / 공지
    private boolean memo = true;
    private boolean question = true;
    private boolean notice = true;
}
