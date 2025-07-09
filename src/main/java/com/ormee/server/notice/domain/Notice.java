package com.ormee.server.notice.domain;

import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.attachment.domain.Attachment;
import com.ormee.server.global.config.EntityTime;
import com.ormee.server.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notice extends EntityTime {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Member author;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    @Column
    private LocalDateTime postDate;

    @Column
    private Boolean isPinned;

    @Column
    private Boolean isDraft;

    @ManyToMany
    @JoinTable(
            name = "notice_likes",
            joinColumns = @JoinColumn(name = "notice_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private List<Member> likes = new ArrayList<>();

    public void removeAttachment(Attachment attachment) {
        this.attachments.remove(attachment);
    }

    public void addLikes(Member student) {
        likes.add(student);
    }

    public void removeLikes(Member student) {
        likes.remove(student);
    }
}
