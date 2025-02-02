package com.ormee.server.dto.lecture;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LectureListDto {
    private List<LectureResponseDto> openLectures;
    private List<LectureResponseDto> closedLectures;
}
