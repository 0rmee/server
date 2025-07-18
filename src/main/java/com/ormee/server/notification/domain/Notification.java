package com.ormee.server.notification.domain;

import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.global.config.EntityTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification extends EntityTime {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @Column
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column
    private Long parentId;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private Boolean isRead;
}
