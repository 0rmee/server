package com.ormee.server.model;

import com.ormee.server.config.AttachmentListener;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AttachmentListener.class)
public class Attachment extends EntityTime {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column
    private String parentId;

    @Column
    @Enumerated(EnumType.STRING)
    private AttachmentType type;

    @Column
    private String fileName;

    @Column
    private String filePath;

    @Column
    private Long fileSize;

    @Column
    private LocalDateTime dueDate;
}
