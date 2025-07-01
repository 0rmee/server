package com.ormee.server.memo.controller;

import com.ormee.server.memo.service.MessageService;
import com.ormee.server.memo.dto.MessageSubmitDto;
import com.ormee.server.global.response.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentMemoController {
    private final MessageService messageService;

    @Autowired
    public StudentMemoController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/memos/{memoId}")
    public ResponseDto submitMessage(@PathVariable Long memoId, @RequestBody MessageSubmitDto messageSubmitDto, Authentication authentication) {
        messageService.submitMessage(memoId, messageSubmitDto, authentication.getName());
        return ResponseDto.success();
    }

    @GetMapping("/memos/{memoId}")
    public ResponseDto readMemo(@PathVariable Long memoId) {
        return ResponseDto.success();
    }

    @GetMapping("/lectures/{lectureId}/memos")
    public ResponseDto readMemos(@PathVariable Long lectureId, Authentication authentication) {
        return ResponseDto.success();
    }
}
