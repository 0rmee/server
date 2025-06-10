package com.ormee.server.repository;

import com.ormee.server.model.Lecture;
import com.ormee.server.model.Notification;
import com.ormee.server.model.NotificationType;
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
}
