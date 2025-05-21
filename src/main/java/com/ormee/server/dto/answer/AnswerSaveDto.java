package com.ormee.server.dto.answer;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
public class AnswerSaveDto {
    private String content;
    private List<MultipartFile> files;
}
