package com.ormee.server.homework.dto;

import lombok.Data;

import java.util.List;

@Data
public class HomeworkSubmitSaveDto {
    private String content;
    private List<Long> fileIds;
}
