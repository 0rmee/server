package com.ormee.server.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubmitRequestDto {
    private String author;
    private String password;
    private List<SubmitDto> submissions;
}
