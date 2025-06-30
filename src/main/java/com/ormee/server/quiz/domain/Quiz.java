package com.ormee.server.quiz.domain;

import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.global.config.EntityTime;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Quiz extends EntityTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @Column
    private Boolean isDraft;

    @Column
    private Boolean isOpened;

    @Column
    private LocalDateTime openTime;

    @Column
    private LocalDateTime dueTime;

    @Column
    private Long timeLimit;

    @Column
    private Boolean notified;
}
