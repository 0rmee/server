package com.ormee.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(nullable = false)
    private String context;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String password;
}
