package com.ormee.server.dto.assignment;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class AssignmentSubmitSaveDto {
    private String content;
    private List<MultipartFile> files;
}
