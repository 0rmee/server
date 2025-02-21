package com.ormee.server.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Quiz extends EntityTime {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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
    private Integer submitStudents;
}
