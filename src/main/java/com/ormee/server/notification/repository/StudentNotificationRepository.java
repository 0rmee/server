package com.ormee.server.notification.repository;

import com.ormee.server.notification.domain.NotificationType;
import com.ormee.server.notification.domain.StudentNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentNotificationRepository extends JpaRepository<StudentNotification, Long> {
    @Query("SELECT s FROM StudentNotification s " +
            "WHERE s.memberId = :memberId AND " +
            "(LOWER(s.header) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.body) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY s.createdAt DESC")
    List<StudentNotification> findAllByMemberIdAndKeyword(@Param("memberId") Long memberId, @Param("keyword") String keyword);
    List<StudentNotification> findAllByMemberIdAndTypeInOrderByCreatedAtDesc(Long memberId, List<NotificationType> types);
    long countAllByMemberIdAndTypeInAndIsReadFalse(Long memberId, List<NotificationType> types);

    Long countAllByMemberIdAndIsReadFalse(Long id);
}
