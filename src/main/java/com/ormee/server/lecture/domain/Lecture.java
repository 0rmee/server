package com.ormee.server.lecture.domain;

import com.ormee.server.global.config.EntityTime;
import com.ormee.server.member.domain.Member;
import com.ormee.server.notice.domain.Notice;
import com.ormee.server.notification.domain.Notification;
import com.ormee.server.question.domain.Question;
import com.ormee.server.quiz.domain.Quiz;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Lecture extends EntityTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Member teacher;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @ElementCollection(targetClass = LectureDay.class)
    @CollectionTable(name = "lecture_day", joinColumns = @JoinColumn(name = "lecture_id"))
    @Column
    @Enumerated(EnumType.STRING)
    private List<LectureDay> lectureDays;

    @Column
    private LocalTime startTime;

    @Column
    private LocalTime endTime;

    @Column
    private LocalDateTime startDate;

    @Column
    private LocalDateTime dueDate;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "lecture_collaborators",
            joinColumns = @JoinColumn(name = "lecture_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"lecture_id", "member_id"})
    )
    private List<Member> collaborators = new ArrayList<>();

    @Column
    private Long collaboratorChangeCount = 0L;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentLecture> studentLectures = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notice> notices = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quiz> quizzes = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    public void addStudentLecture(StudentLecture studentLecture) {
        studentLectures.add(studentLecture);
    }

    public void addCollaborator(Member collaborator) {
        collaborators.add(collaborator);
        if (collaboratorChangeCount == null) {
            collaboratorChangeCount = 1L;
        } else {
            collaboratorChangeCount++;
        }
    }

    public void removeCollaborator(Member collaborator) {
        collaborators.remove(collaborator);
    }
}
