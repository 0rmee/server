package com.ormee.server.dto.memo;

import lombok.Data;

@Data
public class MessageSubmitDto {
    private String context;
    private String author;
    private String password;
}
