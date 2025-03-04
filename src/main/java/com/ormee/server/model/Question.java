package com.ormee.server.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Question extends EntityTime {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @Column(nullable = false)
    private String title;

    @Column
    private String author;

    @Column
    private String content;

    @Column
    private Boolean isAnswered;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;
}
