package com.ormee.server.notification.service;

import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.member.domain.Member;
import com.ormee.server.member.domain.Role;
import com.ormee.server.member.repository.MemberRepository;
import com.ormee.server.notification.domain.NotificationSetting;
import com.ormee.server.notification.dto.NotificationSettingDto;
import com.ormee.server.notification.repository.NotificationSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationSettingService {
    private final NotificationSettingRepository notificationSettingRepository;
    private final MemberRepository memberRepository;

    public NotificationSettingService(NotificationSettingRepository notificationSettingRepository, MemberRepository memberRepository) {
        this.notificationSettingRepository = notificationSettingRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public NotificationSettingDto getSetting(String username) {
        Member student = memberRepository.findByUsernameAndRole(username, Role.STUDENT).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        NotificationSetting setting = notificationSettingRepository.findByMemberId(student.getId()).orElseThrow(() -> new CustomException(ExceptionType.NOTIFICATION_NOT_FOUND_EXCEPTION));

        return NotificationSettingDto.builder()
                .quizRegister(setting.isQuizRegister())
                .quizRemind(setting.isQuizRemind())
                .quizDeadline(setting.isQuizDeadline())
                .homeworkRegister(setting.isHomeworkRegister())
                .homeworkRemind(setting.isHomeworkRemind())
                .homeworkDeadline(setting.isHomeworkDeadline())
                .memo(setting.isMemo())
                .question(setting.isQuestion())
                .notice(setting.isNotice())
                .build();
    }

    @Transactional
    public void updateSetting(String username, NotificationSettingDto requestDto) {
        Member student = memberRepository.findByUsernameAndRole(username, Role.STUDENT).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        NotificationSetting setting = notificationSettingRepository.findByMemberId(student.getId()).orElseThrow(() -> new CustomException(ExceptionType.NOTIFICATION_NOT_FOUND_EXCEPTION));

        setting.setQuizRegister(requestDto.isQuizRegister());
        setting.setQuizRemind(requestDto.isQuizRemind());
        setting.setQuizDeadline(requestDto.isQuizDeadline());
        setting.setHomeworkRegister(requestDto.isHomeworkRegister());
        setting.setHomeworkRemind(requestDto.isHomeworkRemind());
        setting.setHomeworkDeadline(requestDto.isHomeworkDeadline());
        setting.setMemo(requestDto.isMemo());
        setting.setQuestion(requestDto.isQuestion());
        setting.setNotice(requestDto.isNotice());
    }
}

