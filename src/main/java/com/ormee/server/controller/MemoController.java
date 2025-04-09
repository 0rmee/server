package com.ormee.server.controller;

import com.ormee.server.dto.memo.MemoDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.MemoService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping
public class MemoController {
    private final MemoService memoService;

    public MemoController(MemoService memoService) {
        this.memoService = memoService;
    }

    @PostMapping("/teachers/{lectureId}/memos")
    public ResponseDto createMemo(@PathVariable UUID lectureId, @RequestBody MemoDto memoDto) {
        return ResponseDto.success(memoService.createMemo(lectureId, memoDto));
    }

    @PutMapping("/teachers/memos/{memoId}/close") // 추후 서비스 로직 추가
    public ResponseDto closeMemo(@PathVariable UUID memoId) {
        return ResponseDto.success();
    }

    @GetMapping("/teachers/{lectureId}/memos")
    public ResponseDto getAllMemos(@PathVariable UUID lectureId) {
        return ResponseDto.success(memoService.getAllMemos(lectureId));
    }

    @GetMapping("/memos/{memoId}/stats") // 추후 서비스 로직 추가
    public ResponseDto getMemoStats(@PathVariable UUID memoId) {
        return ResponseDto.success();
    }

    // 아래는 학생로직입니다

    @PutMapping("{memoId}/toggleIsOpen")
    public ResponseDto toggleIsOpen(@PathVariable long memoId, @RequestParam boolean isOpen) {
        return ResponseDto.success(memoService.toggleIsOpen(memoId, isOpen));
    }

    @GetMapping("/open")
    public ResponseDto getOpenMemo(@PathVariable UUID lectureId) {
        return ResponseDto.success(memoService.getOpenMemo(lectureId));
    }
}
