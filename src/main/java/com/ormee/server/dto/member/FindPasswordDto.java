package com.ormee.server.dto.member;

import lombok.Data;

@Data
public class FindPasswordDto {
    private String username;
    private String phoneNumber;
    private String newPassword;
}
