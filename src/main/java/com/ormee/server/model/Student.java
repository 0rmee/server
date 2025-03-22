package com.ormee.server.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@DiscriminatorValue("Student")
public class Student extends Member {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String phoneNumber;

    @ElementCollection
    @CollectionTable(name = "social_ids", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "social_id")
    private List<String> socialIds;

    @ElementCollection
    @CollectionTable(name = "social_providers", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "social_provider")
    @Enumerated(EnumType.STRING)
    private List<SocialProvider> socialProviders;
}
