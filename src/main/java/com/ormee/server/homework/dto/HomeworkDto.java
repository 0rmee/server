package com.ormee.server.homework.dto;

import com.ormee.server.member.dto.AuthorDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class HomeworkDto {
    private Long id;
    private String author;
    private String authorImage;
    private String title;
    private String description;
    private boolean feedbackCompleted;
    private boolean isSubmitted;
    private List<Long> fileIds;
    private List<String> fileNames;
    private List<String> filePaths;
    private LocalDateTime openTime;
    private LocalDateTime dueTime;
}
