package com.ormee.server.notification.domain;

import com.ormee.server.global.config.EntityTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentNotification extends EntityTime {
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
    private String header;

    @Column
    private String title;

    @Column
    private String body;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private Boolean isRead;
}
