package com.ormee.server.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Lecture extends EntityTime {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @Column(nullable = false, unique = true)
    private Integer code;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @ElementCollection(targetClass = LectureDay.class)
    @CollectionTable(name = "lecture_day", joinColumns = @JoinColumn(name = "lecture_id"))
    @Column
    @Enumerated(EnumType.STRING)
    private List<LectureDay> lectureDays;

    @Column
    private LocalTime startTime;

    @Column
    private LocalTime endTime;

    @Column
    private LocalDateTime startDate;

    @Column
    private LocalDateTime dueDate;

    @Column
    private Integer students;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;
}
