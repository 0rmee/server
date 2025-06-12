package com.ormee.server.dto.homework;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HomeworkSubmitDto {
    String name;
    String content;
    List<String> filePaths;
    String createdAt;
}
