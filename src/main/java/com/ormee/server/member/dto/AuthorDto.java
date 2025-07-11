package com.ormee.server.member.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorDto {
    private String name;
    private String image;
}