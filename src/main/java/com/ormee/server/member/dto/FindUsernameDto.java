package com.ormee.server.member.dto;

import lombok.Data;

@Data
public class FindUsernameDto {
    private String name;
    private String phoneNumber;
}
