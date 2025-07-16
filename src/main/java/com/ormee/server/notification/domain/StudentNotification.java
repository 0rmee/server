package com.ormee.server.notification.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long memberId;

    @Column
    private Long parentId;

    @Column
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column
    private String title;

    @Column
    private String body;
}
