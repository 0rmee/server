package com.ormee.server.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher extends EntityTime{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private Integer code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nameEng;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String introduce;

    @Column(nullable = true)
    private String image;

    @ElementCollection
    @CollectionTable(name = "social_ids", joinColumns = @JoinColumn(name = "teacher_id"))
    @Column(name = "social_id")
    private List<String> socialIds;

    @ElementCollection
    @CollectionTable(name = "social_providers", joinColumns = @JoinColumn(name = "teacher_id"))
    @Column(name = "social_provider")
    @Enumerated(EnumType.STRING)
    private List<SocialProvider> socialProviders;
}
