package com.ormee.server.homework.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HomeworkSubmitDto {
    Long id;
    String name;
    String content;
    List<String> filePaths;
    String createdAt;
}
