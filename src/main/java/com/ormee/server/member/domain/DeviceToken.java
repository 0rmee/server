package com.ormee.server.member.domain;

import com.ormee.server.global.config.EntityTime;
import jakarta.persistence.*;
import lombok.Setter;

@Entity
@Setter
public class DeviceToken extends EntityTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long memberId;

    @Column
    private String deviceToken;
}
