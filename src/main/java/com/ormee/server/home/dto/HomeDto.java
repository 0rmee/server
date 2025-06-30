package com.ormee.server.home.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HomeDto {
    LectureDto lecture;
    List<ListDto> assignments;
    List<ListDto> questions;
    List<ListDto> notices;
}
