package com.ormee.server.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageListDto {
    private Integer contentDetail;
    private float submitRate;
    private Integer submit;
}
