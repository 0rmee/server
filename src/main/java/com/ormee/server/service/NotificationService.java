package com.ormee.server.service;

import com.ormee.server.repository.LectureRepository;
import com.ormee.server.repository.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final LectureRepository lectureRepository;

    public NotificationService(NotificationRepository notificationRepository, LectureRepository lectureRepository) {
        this.notificationRepository = notificationRepository;
        this.lectureRepository = lectureRepository;
    }

    // 알림 구현
}
