package com.ormee.server.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Attachment extends EntityTime {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "notice_id")
    private Notice notice;

    @Column
    @Enumerated(EnumType.STRING)
    private AttachmentType type;

    @Column
    private String fileName;

    @Column
    private String filePath;

    @Column
    private String fileSize;

    @Column
    private LocalDateTime dueDate;
}
