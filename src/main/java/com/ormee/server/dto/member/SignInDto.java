package com.ormee.server.dto.member;

import lombok.Data;

@Data
public class SignInDto {
    private String username;
    private String password;
}