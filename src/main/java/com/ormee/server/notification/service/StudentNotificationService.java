package com.ormee.server.notification.service;

import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.lecture.repository.StudentLectureRepository;
import com.ormee.server.member.domain.DeviceToken;
import com.ormee.server.member.repository.DeviceTokenRepository;
import com.ormee.server.notification.domain.StudentNotification;
import com.ormee.server.notification.dto.StudentNotificationRequestDto;
import com.ormee.server.notification.repository.StudentNotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentNotificationService {
    private final StudentNotificationRepository studentNotificationRepository;
    private final StudentLectureRepository studentLectureRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final FcmService fcmService;
    public StudentNotificationService(StudentNotificationRepository studentNotificationRepository, StudentLectureRepository studentLectureRepository, DeviceTokenRepository deviceTokenRepository, FcmService fcmService) {
        this.studentNotificationRepository = studentNotificationRepository;
        this.studentLectureRepository = studentLectureRepository;
        this.deviceTokenRepository = deviceTokenRepository;
        this.fcmService = fcmService;
    }

    @Transactional
    public void create(List<Long> targets, StudentNotificationRequestDto studentNotificationRequestDto) throws Exception {
        for(Long memberId : targets) {
            StudentNotification studentNotification = StudentNotification.builder()
                    .memberId(memberId)
                    .parentId(studentNotificationRequestDto.getParentId())
                    .type(studentNotificationRequestDto.getType())
                    .header(studentNotificationRequestDto.getHeader())
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
}
