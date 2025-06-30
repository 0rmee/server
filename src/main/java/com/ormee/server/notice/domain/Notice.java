package com.ormee.server.notice.domain;

import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.attachment.domain.Attachment;
import com.ormee.server.global.config.EntityTime;
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

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    @Column
    private LocalDateTime postDate;

    @Column
    private Boolean isPinned;

    @Column
    private Boolean isDraft;

    @Column
    private Long likes;

    public void removeAttachment(Attachment attachment) {
        this.attachments.remove(attachment);
    }

}
