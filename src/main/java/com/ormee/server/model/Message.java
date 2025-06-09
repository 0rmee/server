package com.ormee.server.model;

import com.ormee.server.model.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Message extends EntityTime{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "memo_id")
    private Memo memo;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Member student;

    @Column(nullable = false)
    private String context;
}
