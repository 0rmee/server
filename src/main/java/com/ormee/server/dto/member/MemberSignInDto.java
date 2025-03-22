package com.ormee.server.config.jwt;

import lombok.Data;

@Data
public class MemberSignInDto {
    private String loginId;
    private String password;
}
