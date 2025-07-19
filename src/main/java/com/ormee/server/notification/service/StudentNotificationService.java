package com.ormee.server.notification.service;

import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.member.domain.DeviceToken;
import com.ormee.server.member.domain.Member;
import com.ormee.server.member.domain.Role;
import com.ormee.server.member.repository.DeviceTokenRepository;
import com.ormee.server.member.repository.MemberRepository;
import com.ormee.server.notification.domain.NotificationType;
import com.ormee.server.notification.domain.StudentNotification;
import com.ormee.server.notification.dto.StudentNotificationDto;
import com.ormee.server.notification.dto.StudentNotificationListDto;
import com.ormee.server.notification.dto.StudentNotificationRequestDto;
import com.ormee.server.notification.repository.StudentNotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentNotificationService {
    private final StudentNotificationRepository studentNotificationRepository;
    private final MemberRepository memberRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final FcmService fcmService;
    public StudentNotificationService(StudentNotificationRepository studentNotificationRepository, MemberRepository memberRepository, DeviceTokenRepository deviceTokenRepository, FcmService fcmService) {
        this.studentNotificationRepository = studentNotificationRepository;
        this.memberRepository = memberRepository;
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
                    .content(studentNotificationRequestDto.getContent())
                    .isRead(false)
                    .build();
            studentNotificationRepository.save(studentNotification);

            List<String> targetTokens = deviceTokenRepository.findAllByMemberId(studentNotification.getMemberId()).stream().map(DeviceToken::getDeviceToken).toList();
            for(String targetToken : targetTokens) {
                fcmService.sendMessageTo(targetToken, studentNotification);
            }
        }
    }

    public StudentNotificationListDto getByType(String type, String username) {
        Member student = memberRepository.findByUsernameAndRole(username, Role.STUDENT).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        List<StudentNotification> notifications;
        if ("과제".equals(type)) {
            notifications = studentNotificationRepository.findAllByMemberIdAndTypeInOrderByCreatedAtDesc(
                    student.getId(),
                    List.of(NotificationType.QUIZ, NotificationType.HOMEWORK)
            );
        } else {
            notifications = studentNotificationRepository.findAllByMemberIdAndTypeInOrderByCreatedAtDesc(
                    student.getId(),
                    List.of(NotificationType.fromKorean(type))
            );
        }

        long unreadCount;
        if ("과제".equals(type)) {
            unreadCount = studentNotificationRepository.countAllByMemberIdAndTypeInAndIsReadFalse(
                    student.getId(),
                    List.of(NotificationType.QUIZ, NotificationType.HOMEWORK)
            );
        } else {
            unreadCount = studentNotificationRepository.countAllByMemberIdAndTypeInAndIsReadFalse(
                    student.getId(),
                    List.of(NotificationType.fromKorean(type))
            );
        }

        return StudentNotificationListDto.builder()
                .count(unreadCount)
                .notifications(notifications.stream().map(StudentNotificationDto::toDto).toList())
                .build();
    }

    public List<StudentNotificationDto> search(String keyword, String username) {
        Member student = memberRepository.findByUsernameAndRole(username, Role.STUDENT).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        List<StudentNotification> notifications = studentNotificationRepository.findAllByMemberIdAndKeyword(student.getId(), keyword);

        return notifications.stream().map(StudentNotificationDto::toDto).toList();
    }

    public void read(Long notificationId) {
        StudentNotification studentNotification = studentNotificationRepository.findById(notificationId).orElseThrow(() -> new CustomException(ExceptionType.NOTIFICATION_NOT_FOUND_EXCEPTION));
        studentNotification.setIsRead(true);
        studentNotificationRepository.save(studentNotification);
    }

    public void delete(Long notificationId) {
        studentNotificationRepository.deleteById(notificationId);
    }
}
