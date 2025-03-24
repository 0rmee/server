package com.ormee.server.dto.student;

import lombok.Data;

@Data
public class StudentTokenDto {
    private String accessToken;
    private String refreshToken;
}
