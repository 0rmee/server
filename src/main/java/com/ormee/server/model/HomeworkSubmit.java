package com.ormee.server.model;

import com.ormee.server.model.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkSubmit extends EntityTime {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "homework_id")
    private Homework homework;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Member student;

    @Column
    private String content;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    @Column
    private Boolean isChecked;

    @Column
    private Boolean isFeedback;
}
