package com.ormee.server.dto.student;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentInfoDto {
    private String name;
    private String description;
    private String email;
    private String password;
    private String phoneNumber;
}
