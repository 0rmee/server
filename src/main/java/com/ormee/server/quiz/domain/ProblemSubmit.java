package com.ormee.server.quiz.domain;

import com.ormee.server.member.domain.Member;
import com.ormee.server.global.config.EntityTime;
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

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Member student;

    @Column(nullable = false)
    private String content;
}
