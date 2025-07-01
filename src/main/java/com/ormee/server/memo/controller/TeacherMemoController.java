package com.ormee.server.memo.controller;

import com.ormee.server.memo.service.MemoService;
import com.ormee.server.memo.service.MessageService;
import com.ormee.server.memo.dto.MemoDto;
import com.ormee.server.global.response.ResponseDto;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teachers")
public class TeacherMemoController {
    private final MemoService memoService;
    private final MessageService messageService;

    public TeacherMemoController(MemoService memoService, MessageService messageService) {
        this.memoService = memoService;
        this.messageService = messageService;
    }

    @PostMapping("/{lectureId}/memos")
    public ResponseDto createMemo(@PathVariable Long lectureId, @RequestBody MemoDto memoDto, Authentication authentication) {
        memoService.createMemo(lectureId, memoDto, authentication.getName());
        return ResponseDto.success();
    }

    @PutMapping("/memos/{memoId}/close")
    public ResponseDto closeMemo(@PathVariable Long memoId) {
        memoService.toggleIsOpen(memoId, false);
        return ResponseDto.success();
    }

    @GetMapping("/{lectureId}/memos")
    public ResponseDto getAllMemos(@PathVariable Long lectureId) {
        return ResponseDto.success(memoService.getAllMemos(lectureId));
    }

    @GetMapping("/memos/{memoId}/stats")
    public ResponseDto getMemoStats(@PathVariable Long memoId) {
        return ResponseDto.success(messageService.getMessages(memoId));
    }
}
