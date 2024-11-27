package com.ormee.server.controller;

import com.ormee.server.dto.MemoDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.MemoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teacher/{lectureId}/memos")
public class MemoController {
    private final MemoService memoService;

    public MemoController(MemoService memoService) {
        this.memoService = memoService;
    }

    @GetMapping
    public ResponseDto getAllMemos() {
        return ResponseDto.success(memoService.getAllMemos());
    }

    @PostMapping
    public ResponseDto createMemo(@RequestBody MemoDto memoDto) {
        return ResponseDto.success(memoService.createMemo(memoDto));
    }

    @PutMapping("{memoId}/toggleIsOpen")
    public ResponseDto toggleIsOpen(@PathVariable long memoId, @RequestParam boolean isOpen) {
        return ResponseDto.success(memoService.toggleIsOpen(memoId, isOpen));
    }
}
