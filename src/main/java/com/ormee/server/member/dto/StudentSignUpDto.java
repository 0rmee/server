package com.ormee.server.member.dto;

import lombok.Data;

@Data
public class StudentSignUpDto {
    private String username;
    private String password;
    private String phoneNumber;
    private String name;
}
