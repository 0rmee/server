package com.ormee.server.lecture.domain;

import com.ormee.server.member.domain.Member;
import com.ormee.server.global.config.EntityTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentLecture extends EntityTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Member student;

    @Column
    private String description;

    @Column
    private Boolean blocked = false;
}
