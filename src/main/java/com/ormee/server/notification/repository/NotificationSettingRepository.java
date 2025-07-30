package com.ormee.server.notification.repository;

import com.ormee.server.notification.domain.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
    Optional<NotificationSetting> findByMemberId(Long memberId);

    List<NotificationSetting> findAllByMemberId(Long memberId);

    Optional<NotificationSetting> findByMemberIdAndDeviceToken(Long id, String token);
}
