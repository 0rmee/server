package com.ormee.server.homework.domain;

import com.ormee.server.attachment.domain.Attachment;
import com.ormee.server.global.config.EntityTime;
import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Homework extends EntityTime {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Member author;

    @Column
    private String title;

    @Column
    private String description;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    @Column
    private Boolean isDraft;

    @Column
    private LocalDateTime openTime;

    @Column
    private LocalDateTime dueTime;

    @Column
    private Boolean notified;

    public void removeAttachment(Attachment attachment) {
        this.attachments.remove(attachment);
    }
}
