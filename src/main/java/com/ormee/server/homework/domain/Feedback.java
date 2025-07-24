package com.ormee.server.homework.domain;

import com.ormee.server.global.config.EntityTime;
import com.ormee.server.member.domain.Member;
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

    @ManyToOne
    @JoinColumn(name = "homework_submit_id")
    private HomeworkSubmit homeworkSubmit;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Member author;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    @Enumerated(EnumType.STRING)
    private StampType stampType;
}
