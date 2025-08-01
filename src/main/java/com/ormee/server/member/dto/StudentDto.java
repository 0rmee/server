package com.ormee.server.member.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentDto {
    private String name;
    private String username;
    private String phoneNumber;
    private String email;
}
