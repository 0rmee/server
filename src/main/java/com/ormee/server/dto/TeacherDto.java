package com.ormee.server.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeacherDto {
    private String name;
    private String email;
    private String phoneNumber;
}
