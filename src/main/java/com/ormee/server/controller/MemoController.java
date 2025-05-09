package com.ormee.server.controller;

import com.ormee.server.dto.memo.MemoDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.MemoService;
import com.ormee.server.service.MessageService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class MemoController {
    private final MemoService memoService;
    private final MessageService messageService;

    public MemoController(MemoService memoService, MessageService messageService) {
        this.memoService = memoService;
        this.messageService = messageService;
    }

    @PostMapping("/teachers/{lectureId}/memos")
    public ResponseDto createMemo(@PathVariable Long lectureId, @RequestBody MemoDto memoDto) {
        return ResponseDto.success(memoService.createMemo(lectureId, memoDto));
    }

    @PutMapping("/teachers/memos/{memoId}/close")
    public ResponseDto closeMemo(@PathVariable Long memoId) {
        return ResponseDto.success(memoService.toggleIsOpen(memoId, false));
    }

    @GetMapping("/teachers/{lectureId}/memos")
    public ResponseDto getAllMemos(@PathVariable Long lectureId) {
        return ResponseDto.success(memoService.getAllMemos(lectureId));
    }

    @GetMapping("/memos/{memoId}/stats")
    public ResponseDto getMemoStats(@PathVariable Long memoId) {
        return ResponseDto.success(messageService.getMessages(memoId));
    }

    // 아래는 학생로직입니다

    @PutMapping("{memoId}/toggleIsOpen")
    public ResponseDto toggleIsOpen(@PathVariable long memoId, @RequestParam boolean isOpen) {
        return ResponseDto.success(memoService.toggleIsOpen(memoId, isOpen));
    }

    @GetMapping("/open")
    public ResponseDto getOpenMemo(@PathVariable Long lectureId) {
        return ResponseDto.success(memoService.getOpenMemo(lectureId));
    }
}
