package com.ormee.server.notification.repository;

import com.ormee.server.notification.domain.StudentNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentNotificationRepository extends JpaRepository<StudentNotification, Long> {
}
