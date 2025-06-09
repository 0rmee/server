package com.ormee.server.dto.assignment;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AssignmentSubmitDto {
    String name;
    String content;
    List<String> filePaths;
    String createdAt;
}
