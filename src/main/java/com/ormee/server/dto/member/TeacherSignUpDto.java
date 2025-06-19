package com.ormee.server.dto.member;

import lombok.Data;

@Data
public class TeacherSignUpDto {
    private String username;
    private String password;
    private String phoneNumber;
    private String email;
    private String name;
    private String nickname;
}
