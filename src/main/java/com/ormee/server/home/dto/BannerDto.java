package com.ormee.server.home.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BannerDto {
    private Long id;
    private String image;
    private String path;
}
