package com.ormee.server.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class SignInDto {
    private UUID id;
    private String password;
}
