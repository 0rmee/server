package com.ormee.server.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProblemSubmit extends EntityTime {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @OneToOne
    @JoinColumn(name = "student_lecture_id")
    private StudentLecture studentLecture;

    @Column(nullable = false)
    private String content;
}
