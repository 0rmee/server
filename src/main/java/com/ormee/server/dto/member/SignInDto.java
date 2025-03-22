package com.ormee.server.dto.member;

import lombok.Data;

import java.util.UUID;

@Data
public class SignInDto {
    private Integer code;
    private String password;
}