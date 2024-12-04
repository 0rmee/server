package com.ormee.server.dto.memo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MemoListDto {
    private List<MemoDto> openMemos;
    private List<MemoDto> closeMemos;
}
