package com.ormee.server.dto;

import lombok.Data;

@Data
public class SubmitDto {
    private Long problemId;
    private String content;
}
