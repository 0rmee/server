package com.ormee.server.controller;

import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.model.AttachmentType;
import com.ormee.server.service.attachment.AttachmentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class AttachmentController {
    private final AttachmentService attachmentService;


    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/attachment")
    public ResponseDto uploadAttachment(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseDto.success(attachmentService.save(AttachmentType.QUIZ,  -1L, file).getId());
    }

    @DeleteMapping("/attachment/{attachmentId}/quizzes")
    public ResponseDto deleteQuizAttachment(@PathVariable Long attachmentId) {
        attachmentService.delete(attachmentId);
        return ResponseDto.success();
    }
}
