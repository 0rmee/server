package com.ormee.server.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Assignment extends EntityTime {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private Boolean isDraft;

    @Column
    private LocalDateTime openTime;

    @Column
    private LocalDateTime dueTime;
}
