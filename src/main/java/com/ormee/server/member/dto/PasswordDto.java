package com.ormee.server.member.dto;

import lombok.Data;

@Data
public class PasswordDto {
    private String password;
    private String newPassword;
}
