package com.ormee.server.dto.member;

import lombok.Data;

@Data
public class MemberSignInDto {
    private String email;
    private String password;
}