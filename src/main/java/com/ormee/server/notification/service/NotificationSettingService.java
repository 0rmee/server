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

import java.util.List;

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
        NotificationSetting setting = notificationSettingRepository.findFirstByMemberId(student.getId()).orElseThrow(() -> new CustomException(ExceptionType.NOTIFICATION_NOT_FOUND_EXCEPTION));

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
                .event(setting.isEvent())
                .build();
    }

    @Transactional
    public void updateSetting(String username, NotificationSettingDto requestDto) {
        Member student = memberRepository.findByUsernameAndRole(username, Role.STUDENT).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        List<NotificationSetting> settings = notificationSettingRepository.findAllByMemberId(student.getId());

        if (settings.isEmpty()) {
            throw new CustomException(ExceptionType.NOTIFICATION_NOT_FOUND_EXCEPTION);
        }

        for (NotificationSetting setting : settings) {
            if (requestDto.getQuizRegister() != null) setting.setQuizRegister(requestDto.getQuizRegister());
            if (requestDto.getQuizRemind() != null) setting.setQuizRemind(requestDto.getQuizRemind());
            if (requestDto.getQuizDeadline() != null) setting.setQuizDeadline(requestDto.getQuizDeadline());

            if (requestDto.getHomeworkRegister() != null) setting.setHomeworkRegister(requestDto.getHomeworkRegister());
            if (requestDto.getHomeworkRemind() != null) setting.setHomeworkRemind(requestDto.getHomeworkRemind());
            if (requestDto.getHomeworkDeadline() != null) setting.setHomeworkDeadline(requestDto.getHomeworkDeadline());

            if (requestDto.getMemo() != null) setting.setMemo(requestDto.getMemo());
            if (requestDto.getQuestion() != null) setting.setQuestion(requestDto.getQuestion());
            if (requestDto.getNotice() != null) setting.setNotice(requestDto.getNotice());
            if (requestDto.getEvent() != null) setting.setEvent(requestDto.getEvent());
        }
    }
}

