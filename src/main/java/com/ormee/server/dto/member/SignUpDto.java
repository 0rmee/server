package com.ormee.server.dto.member;

import com.ormee.server.model.Teacher;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpDto {
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
    private String image;

    public static Teacher toEntity(SignUpDto signUpDto, Integer code) {
        return Teacher.teacherBuilder()
                .code(code)
                .name(signUpDto.name)
                .email(signUpDto.email)
                .phoneNumber(signUpDto.phoneNumber)
                .password(signUpDto.password)
                .image(signUpDto.image)
                .build();
    }
}
