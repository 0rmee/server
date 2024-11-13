package com.ormee.server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Lecture extends EntityTime {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @ElementCollection(targetClass = LectureDays.class)
    @CollectionTable(name = "lectureDay", joinColumns = @JoinColumn(name = "lecture_id"))
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private List<LectureDays> lectureDay;

    @Column(nullable = false)
    private LocalTime lectureTime;

    @Column(nullable = false)
    private LocalDateTime dueTime;
}
