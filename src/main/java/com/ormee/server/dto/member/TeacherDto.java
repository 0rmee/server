package com.ormee.server.dto.member;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeacherDto {
    private String username;
    private String name;
    private String nickname;
    private String email;
    private String phoneNumber;
    private String introduction;
    private String image;
}
