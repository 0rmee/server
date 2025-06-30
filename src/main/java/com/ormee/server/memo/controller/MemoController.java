package com.ormee.server.memo.controller;

import com.ormee.server.memo.service.MemoService;
import com.ormee.server.memo.service.MessageService;
import com.ormee.server.memo.dto.MemoDto;
import com.ormee.server.global.response.ResponseDto;
import org.springframework.security.core.Authentication;
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
    public ResponseDto createMemo(@PathVariable Long lectureId, @RequestBody MemoDto memoDto, Authentication authentication) {
        memoService.createMemo(lectureId, memoDto, authentication.getName());
        return ResponseDto.success();
    }

    @PutMapping("/teachers/memos/{memoId}/close")
    public ResponseDto closeMemo(@PathVariable Long memoId) {
        memoService.toggleIsOpen(memoId, false);
        return ResponseDto.success();
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
        memoService.toggleIsOpen(memoId, isOpen);
        return ResponseDto.success();
    }

    @GetMapping("/open")
    public ResponseDto getOpenMemo(@PathVariable Long lectureId) {
        return ResponseDto.success(memoService.getOpenMemo(lectureId));
    }
}
