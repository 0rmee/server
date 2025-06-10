package com.ormee.server.service;

import com.ormee.server.dto.notification.NotificationDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.*;
import com.ormee.server.repository.LectureRepository;
import com.ormee.server.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final LectureRepository lectureRepository;

    public NotificationService(NotificationRepository notificationRepository, LectureRepository lectureRepository) {
        this.notificationRepository = notificationRepository;
        this.lectureRepository = lectureRepository;
    }

    public void create(NotificationType type, Object parent) {
        Lecture lecture;
        Long parentId;
        String title;
        String description;

        switch (type) {
            case QUIZ -> {
                Quiz quiz = (Quiz) parent;
                lecture = quiz.getLecture();
                parentId = quiz.getId();
                title = lecture.getTitle();
                description = "퀴즈가 마감되었어요.";
            }
            case ASSIGNMENT -> {
                Assignment assignment = (Assignment) parent;
                lecture = assignment.getLecture();
                parentId = assignment.getId();
                title = lecture.getTitle();
                description = "숙제가 마감되었어요.";
            }
            case MEMO -> {
                Memo memo = (Memo) parent;
                lecture = memo.getLecture();
                parentId = memo.getId();
                title = lecture.getTitle();
                description = "쪽지가 마감되었어요.";
            }
            case QUESTION -> {
                Question question = (Question) parent;
                lecture = question.getLecture();
                parentId = question.getId();
                title = lecture.getTitle();
                description = question.getStudent().getName() + " 학생이 질문을 등록했어요.";
            }
            default -> throw new IllegalArgumentException("지원하지 않는 알림 타입");
        }

        Notification notification = Notification.builder()
                .lecture(lecture)
                .type(type)
                .parentId(parentId)
                .title(title)
                .description(description)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }

    public List<NotificationDto> getNotificationsByLectureIdAndType(Long lectureId, String filter) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Notification> notifications = switch (filter) {
            case "퀴즈" -> notificationRepository.findAllByLectureAndTypeOrderByCreatedAtDesc(lecture, NotificationType.QUIZ);
            case "쪽지" -> notificationRepository.findAllByLectureAndTypeOrderByCreatedAtDesc(lecture, NotificationType.MEMO);
            case "숙제" -> notificationRepository.findAllByLectureAndTypeOrderByCreatedAtDesc(lecture, NotificationType.ASSIGNMENT);
            case "질문" -> notificationRepository.findAllByLectureAndTypeOrderByCreatedAtDesc(lecture, NotificationType.QUESTION);
            default -> notificationRepository.findAllByLectureOrderByCreatedAtDesc(lecture);
        };

        return notifications.stream()
                .map(notification -> NotificationDto.builder()
                        .notificationId(notification.getId())
                        .type(String.valueOf(notification.getType()))
                        .title(notification.getTitle())
                        .description(notification.getDescription())
                        .parentId(notification.getParentId())
                        .isRead(notification.getIsRead() != null && notification.getIsRead())
                        .createdAt(notification.getCreatedAt())
                        .build())
                .toList();
    }

    @Transactional
    public void markAllAsRead(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Notification> notifications = notificationRepository.findAllByLectureOrderByCreatedAtDesc(lecture);

        notifications.forEach(notification -> notification.setIsRead(true));
    }

    public void deleteAll(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        notificationRepository.deleteAllByLecture(lecture);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new CustomException(ExceptionType.NOTIFICATION_NOT_FOUND_EXCEPTION));
        notification.setIsRead(true);
    }

    public void deleteById(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new CustomException(ExceptionType.NOTIFICATION_NOT_FOUND_EXCEPTION));
        notificationRepository.delete(notification);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteAllExpiredNotifications() {
        notificationRepository.deleteAllByCreatedAtBefore(LocalDateTime.now().minusDays(30));
    }
}
