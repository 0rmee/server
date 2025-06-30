package com.ormee.server.homework.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HomeworkListDto {
    List<HomeworkDto> openedHomeworks;
    List<HomeworkDto> closedHomeworks;
}
