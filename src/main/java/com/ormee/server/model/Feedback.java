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
    @JoinColumn(name = "homework_submit_id")
    private HomeworkSubmit homeworkSubmit;

    @Column
    private String content;

    @Column
    @Enumerated(EnumType.STRING)
    private StampType stampType;
}
