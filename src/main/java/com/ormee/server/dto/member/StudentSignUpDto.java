package com.ormee.server.dto.member;

import lombok.Data;

@Data
public class StudentSignUpDto {
    private String username;
    private String password;
    private String phoneNumber;
    private String name;
}
