package com.ormee.server.member.dto;

import lombok.Data;

@Data
public class FindPasswordDto {
    private String username;
    private String phoneNumber;
    private String newPassword;
}
