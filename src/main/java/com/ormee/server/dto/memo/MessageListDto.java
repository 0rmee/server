package com.ormee.server.dto.memo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageListDto {
    private long rank;
    private Integer contentDetail;
    private long submitRate;
    private Integer submit;
}
