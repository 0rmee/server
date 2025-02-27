package com.ormee.server.model;

import jakarta.persistence.*;

@Entity
public class StudentLecture extends EntityTime {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @Column
    private String description;
}
