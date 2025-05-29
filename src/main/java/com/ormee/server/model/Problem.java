package com.ormee.server.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Problem extends EntityTime {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @Column
    @Enumerated(EnumType.STRING)
    private ProblemType type;

    @Column
    private String content;

    @Column
    private String answer;

    @ElementCollection
    @CollectionTable(name = "problem_items", joinColumns = @JoinColumn(name = "problem_id"))
    @Column(name = "item")
    private List<String> items;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();
}
