package com.ormee.server.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "studentBuilder")
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
