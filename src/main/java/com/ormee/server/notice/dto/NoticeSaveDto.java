package com.ormee.server.notice.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
public class NoticeSaveDto {
    private String title;
    private String description;
    private List<MultipartFile> files;
    private Boolean isDraft;
}
