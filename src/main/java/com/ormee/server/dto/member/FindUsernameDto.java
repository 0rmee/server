package com.ormee.server.dto.member;

import lombok.Data;

@Data
public class FindUsernameDto {
    private String name;
    private String phoneNumber;
}
