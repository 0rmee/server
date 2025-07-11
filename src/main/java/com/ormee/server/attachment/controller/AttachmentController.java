package com.ormee.server.attachment.controller;

import com.ormee.server.attachment.service.AttachmentService;
import com.ormee.server.attachment.domain.AttachmentType;
import com.ormee.server.global.response.ResponseDto;
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
    public ResponseDto uploadAttachment(@RequestParam("file") MultipartFile file, @RequestParam("type")
                                        String type) throws IOException {
        return ResponseDto.success(attachmentService.save(AttachmentType.valueOf(type),  -1L, file).getId());
    }

    @DeleteMapping("/attachment/{attachmentId}")
    public ResponseDto deleteAttachment(@PathVariable Long attachmentId) {
        attachmentService.delete(attachmentId);
        return ResponseDto.success();
    }
}
