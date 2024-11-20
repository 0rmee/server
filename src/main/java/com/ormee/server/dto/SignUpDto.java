package com.ormee.server.dto;

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

    public static Teacher toEntity(SignUpDto signUpDto) {
        return Teacher.builder()
                .name(signUpDto.name)
                .email(signUpDto.email)
                .phoneNumber(signUpDto.phoneNumber)
                .password(signUpDto.password)
                .image(signUpDto.image)
                .build();
    }
}
