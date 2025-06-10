package com.ormee.server.service;

import com.ormee.server.dto.notification.NotificationDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.Lecture;
import com.ormee.server.model.Notification;
import com.ormee.server.model.NotificationType;
import com.ormee.server.repository.LectureRepository;
import com.ormee.server.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final LectureRepository lectureRepository;

    public NotificationService(NotificationRepository notificationRepository, LectureRepository lectureRepository) {
        this.notificationRepository = notificationRepository;
        this.lectureRepository = lectureRepository;
    }

    public List<NotificationDto> getNotificationsByLectureIdAndType(Long lectureId, String filter) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Notification> notifications = switch (filter) {
            case "퀴즈" ->
                    notificationRepository.findAllByLectureAndTypeOrderByCreatedAtDesc(lecture, NotificationType.Quiz);
            case "쪽지" ->
                    notificationRepository.findAllByLectureAndTypeOrderByCreatedAtDesc(lecture, NotificationType.Memo);
            case "숙제" ->
                    notificationRepository.findAllByLectureAndTypeOrderByCreatedAtDesc(lecture, NotificationType.Assignment);
            case "질문" ->
                    notificationRepository.findAllByLectureAndTypeOrderByCreatedAtDesc(lecture, NotificationType.QnA);
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
}
