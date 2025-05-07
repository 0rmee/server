package com.ormee.server.dto.member;

import lombok.Data;

@Data
public class PasswordDto {
    private String password;
    private String newPassword;
}
