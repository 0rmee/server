package com.ormee.server.question.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class QuestionSaveDto {
    private String title;
    private String content;
    private List<MultipartFile> files;
}
