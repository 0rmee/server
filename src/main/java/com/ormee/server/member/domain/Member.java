package com.ormee.server.member.domain;

import com.ormee.server.attachment.domain.Attachment;
import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.lecture.domain.StudentLecture;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(
        uniqueConstraints = {
        @UniqueConstraint(columnNames = {"role", "username"}),
        @UniqueConstraint(columnNames = {"role", "phone_number"})
})
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member implements UserDetails {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column
    private String nickname;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column
    private String email;

    @Column
    private String introduction;

    @Column
    private Long loginFailCount = 0L;

    @Column
    private Boolean locked = false;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private Attachment image;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @ElementCollection
    @CollectionTable(name = "member_social_logins", joinColumns = @JoinColumn(name = "member_id"))
    private List<SocialLogin> socialLogins = new ArrayList<>();

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lecture> lectures = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentLecture> studentLectures = new ArrayList<>();

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void addLecture(Lecture lecture) {
        this.lectures.add(lecture);
    }

    public void removeLecture(Lecture lecture) {
        lectures.remove(lecture);
        if (lecture.getTeacher() == this) {
            lecture.setTeacher(null);
        }
    }

    public void addStudentLecture(StudentLecture studentLecture) {
        studentLectures.add(studentLecture);
    }
}
