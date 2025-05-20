package com.ormee.server.dto.notice;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
public class NoticeSaveDto {
    private String title;
    private String description;
    private List<MultipartFile> files;
    private Boolean isPinned;
}
