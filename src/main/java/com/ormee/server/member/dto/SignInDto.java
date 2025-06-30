package com.ormee.server.member.dto;

import lombok.Data;

@Data
public class SignInDto {
    private String username;
    private String password;
}