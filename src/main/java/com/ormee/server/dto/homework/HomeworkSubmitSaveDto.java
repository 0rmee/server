package com.ormee.server.dto.homework;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class HomeworkSubmitSaveDto {
    private String content;
    private List<MultipartFile> files;
}
