package com.ormee.server.member.repository;

import com.ormee.server.member.domain.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    Optional<DeviceToken> findByMemberIdAndDeviceToken(Long memberId, String token);
}
