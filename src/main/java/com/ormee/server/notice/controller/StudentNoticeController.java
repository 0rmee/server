package com.ormee.server.notice.controller;

import com.ormee.server.global.response.ResponseDto;
import com.ormee.server.notice.service.NoticeService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentNoticeController {
    private final NoticeService noticeService;

    public StudentNoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping("/lectures/{lectureId}/notices")
    public ResponseDto getNotices(@PathVariable Long lectureId) {
        return ResponseDto.success();
    }

    @GetMapping("/notices/{noticeId}")
    public ResponseDto getNotice(@PathVariable Long noticeId, Authentication authentication) {
        return ResponseDto.success();
    }

    @PutMapping("/notices/{noticeId}/like")
    public ResponseDto likeNotice(@PathVariable Long noticeId, Authentication authentication) {
        return ResponseDto.success();
    }

    @PutMapping("/notices/{noticeId}/unlike")
    public ResponseDto unlikeNotice(@PathVariable Long noticeId, Authentication authentication) {
        return ResponseDto.success();
    }
}
