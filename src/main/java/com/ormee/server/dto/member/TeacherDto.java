package com.ormee.server.dto.member;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeacherDto {
    private String name;
    private String nameEng;
    private String email;
    private String phoneNumber;
    private String introduce;
    private String image;
}
