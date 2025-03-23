package com.ormee.server.dto.answer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerSaveDto {
    private String content;
}
