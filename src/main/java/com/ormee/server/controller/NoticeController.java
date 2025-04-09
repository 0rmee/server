package com.ormee.server.controller;

import com.ormee.server.dto.notice.NoticeSaveDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.NoticeService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/notices")
public class NoticeController {
    private final NoticeService noticeService;
    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping("/teacher/{lectureId}")
    public ResponseDto createNotice(@PathVariable UUID lectureId, @ModelAttribute NoticeSaveDto noticeSaveDto) throws IOException {
        noticeService.saveNotice(lectureId, noticeSaveDto);
        return ResponseDto.success();
    }

    @PutMapping("/teacher/{noticeId}")
    public ResponseDto updateNotice(@PathVariable Long noticeId, @RequestBody NoticeSaveDto noticeSaveDto) throws IOException {
        noticeService.modifyNotice(noticeId, noticeSaveDto);
        return ResponseDto.success();
    }

    @DeleteMapping("/teacher/{noticeId}")
    public ResponseDto deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return ResponseDto.success();
    }

    @GetMapping("/{lectureId}")
    public ResponseDto getNoticeList(@PathVariable UUID lectureId) {
        return ResponseDto.success(noticeService.findAllByLectureId(lectureId));
    }

    @GetMapping("/{lectureId}/{noticeId}")
    public ResponseDto getNotice(@PathVariable Long noticeId) {
        return ResponseDto.success(noticeService.findById(noticeId));
    }
}
