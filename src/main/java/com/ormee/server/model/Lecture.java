package com.ormee.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @ElementCollection(targetClass = LectureDay.class)
    @CollectionTable(name = "lecture_day", joinColumns = @JoinColumn(name = "lecture_id"))
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private List<LectureDay> lecture_day;

    @Column(nullable = false)
    private LocalTime lecture_time;

    @Column(nullable = false)
    private LocalDateTime due_time;
}
