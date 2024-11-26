package com.ormee.server.controller;

import com.ormee.server.dto.MessageSubmitDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/student/{lectureId}/{memoId}/messages")
    public ResponseDto submitMessage(
            @PathVariable UUID lectureId,
            @PathVariable long memoId,
            @RequestBody MessageSubmitDto messageSubmitDto
    ){
        messageService.submitMessage(lectureId, memoId, messageSubmitDto);
        return ResponseDto.success();
    }

    @GetMapping("/teacher/{lectureId}/{memoId}/messages")
    public ResponseDto getMessages(
            @PathVariable UUID lectureId,
            @PathVariable Long memoId
    ){
        return ResponseDto.success(messageService.getMessages(lectureId, memoId));
    }
}
