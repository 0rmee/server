package com.ormee.server.dto.assignment;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssignmentSaveDto {
    private String title;
    private String description;
    private List<MultipartFile> files;
    private Boolean isDraft;
    private LocalDateTime openTime;
    private LocalDateTime dueTime;
}
