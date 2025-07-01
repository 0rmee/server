package com.ormee.server.member.dto;

import lombok.Data;

@Data
public class SignUpDto {
    private String username;
    private String password;
    private String phoneNumber;
    private String email;
    private String name;
    private String nickname;
}
