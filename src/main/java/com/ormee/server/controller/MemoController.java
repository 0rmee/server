package com.ormee.server.controller;

import com.ormee.server.dto.memo.MemoDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.MemoService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/teacher/{lectureId}/memos")
public class MemoController {
    private final MemoService memoService;

    public MemoController(MemoService memoService) {
        this.memoService = memoService;
    }

    @GetMapping
    public ResponseDto getAllMemos(@PathVariable UUID lectureId) {
        return ResponseDto.success(memoService.getAllMemos(lectureId));
    }

    @PostMapping
    public ResponseDto createMemo(@PathVariable UUID lectureId, @RequestBody MemoDto memoDto) {
        return ResponseDto.success(memoService.createMemo(lectureId, memoDto));
    }

    @PutMapping("{memoId}/toggleIsOpen")
    public ResponseDto toggleIsOpen(@PathVariable long memoId, @RequestParam boolean isOpen) {
        return ResponseDto.success(memoService.toggleIsOpen(memoId, isOpen));
    }

    @GetMapping("/open")
    public ResponseDto getOpenMemo(@PathVariable UUID lectureId) {
        return ResponseDto.success(memoService.getOpenMemo(lectureId));
    }
}
