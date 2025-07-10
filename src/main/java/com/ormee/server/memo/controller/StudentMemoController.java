package com.ormee.server.memo.controller;

import com.ormee.server.memo.service.MemoService;
import com.ormee.server.memo.service.MessageService;
import com.ormee.server.memo.dto.MessageSubmitDto;
import com.ormee.server.global.response.ResponseDto;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentMemoController {
    private final MessageService messageService;
    private final MemoService memoService;

    public StudentMemoController(MessageService messageService, MemoService memoService) {
        this.messageService = messageService;
        this.memoService = memoService;
    }

    @PostMapping("/memos/{memoId}")
    public ResponseDto submitMessage(@PathVariable Long memoId, @RequestBody MessageSubmitDto messageSubmitDto, Authentication authentication) {
        messageService.submitMessage(memoId, messageSubmitDto, authentication.getName());
        return ResponseDto.success();
    }

    @GetMapping("/memos/{memoId}")
    public ResponseDto readMemo(@PathVariable Long memoId, Authentication authentication) {
        return ResponseDto.success(memoService.read(memoId, authentication.getName()));
    }

    @GetMapping("/lectures/{lectureId}/memos")
    public ResponseDto readMemos(@PathVariable Long lectureId, Authentication authentication) {
        return ResponseDto.success(memoService.getMemosByLecture(lectureId, authentication.getName()));
    }
}
