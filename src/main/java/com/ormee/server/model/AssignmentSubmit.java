package com.ormee.server.model;

import jakarta.persistence.*;

@Entity
public class AssignmentSubmit extends EntityTime {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @OneToOne
    @JoinColumn(name = "student_lecture_id")
    private StudentLecture studentLecture;

    @Column
    private String content;

    @Column
    private Boolean isFeedback;
}
