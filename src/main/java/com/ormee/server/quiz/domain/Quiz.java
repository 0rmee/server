package com.ormee.server.quiz.domain;

import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.global.config.EntityTime;
import com.ormee.server.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Quiz extends EntityTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Member author;

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
    private Boolean notified;
}
