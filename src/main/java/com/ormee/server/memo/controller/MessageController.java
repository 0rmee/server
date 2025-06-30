package com.ormee.server.memo.controller;

import com.ormee.server.memo.service.MessageService;
import com.ormee.server.memo.dto.MessageSubmitDto;
import com.ormee.server.global.response.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/student/{memoId}/messages")
    public ResponseDto submitMessage(@PathVariable long memoId, @RequestBody MessageSubmitDto messageSubmitDto, Authentication authentication) {
        messageService.submitMessage(memoId, messageSubmitDto, authentication.getName());
        return ResponseDto.success();
    }

    @GetMapping("/teacher/{memoId}/messages")
    public ResponseDto getMessages(@PathVariable Long memoId) {
        return ResponseDto.success(messageService.getMessages(memoId));
    }
}
