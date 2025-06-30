package com.ormee.server.memo.dto;

import lombok.Data;

@Data
public class MessageSubmitDto {
    private String context;
    private String author;
    private String password;
}
