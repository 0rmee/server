package com.ormee.server.home.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class BannerRequestDto {
    private String image;
    private String iosPath;
    private String aosPath;
}
