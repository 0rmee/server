package com.ormee.server.controller;

import com.ormee.server.dto.memo.MessageSubmitDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/api/student/{memoId}/messages")
    public ResponseDto submitMessage(
            @PathVariable long memoId,
            @RequestBody MessageSubmitDto messageSubmitDto
    ){
        messageService.submitMessage(memoId, messageSubmitDto);
        return ResponseDto.success();
    }

    @GetMapping("/teacher/{memoId}/messages")
    public ResponseDto getMessages(
            @PathVariable Long memoId
    ){
        return ResponseDto.success(messageService.getMessages(memoId));
    }
}
