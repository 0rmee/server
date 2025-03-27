package com.ormee.server.dto.member;

import com.ormee.server.model.Role;
import com.ormee.server.model.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MemberSignUpDto {
    @NotBlank
    @Email(message = "이메일 형식을 확인하세요")
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@$%^&*])[a-zA-Z0-9!@$%^&*]{8,20}",
            message = "영문, 숫자, 특수문자를 포함한 10~20자리로 입력해주세요")
    private String password;

    private Role role;

    public Member toEntity(String encodedPassword){
        return Member.builder()
                .password(encodedPassword)
                .email(this.getEmail())
                .role(this.getRole())
                .build();
    }
}
