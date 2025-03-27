package com.ormee.server.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Feedback extends EntityTime {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne
    @JoinColumn(name = "assignment_submit_id")
    private AssignmentSubmit assignmentSubmit;

    @Column
    private String content;

    // 스티커 타입?
}
