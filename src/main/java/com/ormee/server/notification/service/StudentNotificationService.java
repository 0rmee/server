package com.ormee.server.notification.service;

import com.ormee.server.member.domain.DeviceToken;
import com.ormee.server.member.repository.DeviceTokenRepository;
import com.ormee.server.notification.domain.StudentNotification;
import com.ormee.server.notification.dto.StudentNotificationRequestDto;
import com.ormee.server.notification.repository.StudentNotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentNotificationService {
    private final StudentNotificationRepository studentNotificationRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final FcmService fcmService;
    public StudentNotificationService(StudentNotificationRepository studentNotificationRepository, DeviceTokenRepository deviceTokenRepository, FcmService fcmService) {
        this.studentNotificationRepository = studentNotificationRepository;
        this.deviceTokenRepository = deviceTokenRepository;
        this.fcmService = fcmService;
    }

    public void create(StudentNotificationRequestDto studentNotificationRequestDto) throws Exception {
        StudentNotification studentNotification = StudentNotification.builder()
                .memberId(studentNotificationRequestDto.getMemberId())
                .parentId(studentNotificationRequestDto.getParentId())
                .type(studentNotificationRequestDto.getType())
                .title(studentNotificationRequestDto.getTitle())
                .body(studentNotificationRequestDto.getBody())
                .build();

        studentNotificationRepository.save(studentNotification);

        List<String> targetTokens = deviceTokenRepository.findAllByMemberId(studentNotification.getMemberId()).stream().map(DeviceToken::getDeviceToken).toList();
        for(String targetToken : targetTokens) {
            fcmService.sendMessageTo(targetToken, studentNotification);
        }
    }
}
