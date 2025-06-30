package com.ormee.server.memo.domain;

import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.global.config.EntityTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Memo extends EntityTime {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private LocalDateTime dueTime;

    @Column(nullable = false)
    private Boolean isOpen;

    @Column
    private Boolean notified;
}
