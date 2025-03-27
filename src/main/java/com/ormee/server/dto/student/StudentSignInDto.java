package com.ormee.server.dto.student;

import lombok.Data;

@Data
public class StudentSignInDto {
    private String email;
    private String password;
}
