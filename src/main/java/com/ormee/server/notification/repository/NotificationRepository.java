package com.ormee.server.notification.repository;

import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.notification.domain.Notification;
import com.ormee.server.notification.domain.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByLectureOrderByCreatedAtDesc(Lecture lecture);
    List<Notification> findAllByLectureAndTypeOrderByCreatedAtDesc(Lecture lecture, NotificationType type);
    void deleteAllByLecture(Lecture lecture);
    void deleteAllByCreatedAtBefore(LocalDateTime cutoff);

    List<Notification> findAllByLecture(Lecture lecture);
}
