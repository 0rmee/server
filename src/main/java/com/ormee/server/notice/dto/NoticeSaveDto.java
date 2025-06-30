package com.ormee.server.notice.dto;

import lombok.Data;
import java.util.List;

@Data
public class NoticeSaveDto {
    private String title;
    private String description;
    private List<Long> fileIds;
    private Boolean isDraft;
}
