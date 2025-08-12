package com.ormee.server.notice.dto;

import com.ormee.server.attachment.domain.AttachmentType;
import com.ormee.server.member.dto.AuthorDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class NoticeDto {
    private String title;
    private String description;
    private List<Long> fileIds;
    private List<String> fileNames;
    private List<String> filePaths;
    private List<AttachmentType> fileTypes;
    private LocalDateTime postDate;
    private Boolean isPinned;
    private Boolean isLiked;
    private int likes;
    private AuthorDto author;
}
