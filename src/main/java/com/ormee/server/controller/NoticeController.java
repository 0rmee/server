package com.ormee.server.controller;

import com.ormee.server.dto.notice.NoticeSaveDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.NoticeService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping
public class NoticeController {
    private final NoticeService noticeService;
    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping("/teachers/{lectureId}/notices")
    public ResponseDto createNotice(@PathVariable Long lectureId, @ModelAttribute NoticeSaveDto noticeSaveDto) throws IOException {
        noticeService.saveNotice(lectureId, noticeSaveDto);
        return ResponseDto.success();
    }

    @GetMapping("/teachers/{lectureId}/notices/search")
    public ResponseDto searchNotice(@PathVariable Long lectureId, @RequestParam String keyword) {
        return ResponseDto.success(noticeService.findByKeyword(lectureId, keyword));
    }

    @PutMapping("/teachers/notices/{noticeId}")
    public ResponseDto updateNotice(@PathVariable Long noticeId, @ModelAttribute NoticeSaveDto noticeSaveDto) throws IOException {
        noticeService.modifyNotice(noticeId, noticeSaveDto);
        return ResponseDto.success();
    }

    @PutMapping("/teachers/notices/{noticeId}/pin")
    public ResponseDto updatePinNotice(@PathVariable Long noticeId) {
        noticeService.pin(noticeId, true);
        return ResponseDto.success();
    }

    @PutMapping("/teachers/notices/{noticeId}/unpin")
    public ResponseDto updateUnPinNotice(@PathVariable Long noticeId) {
        noticeService.pin(noticeId, false);
        return ResponseDto.success();
    }

    @DeleteMapping("/teachers/notices/{noticeId}")
    public ResponseDto deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return ResponseDto.success();
    }

    @GetMapping("/teachers/{lectureId}/notices")
    public ResponseDto getNoticeList(@PathVariable Long lectureId) {
        return ResponseDto.success(noticeService.findAllByLectureId(lectureId));
    }

    @GetMapping("/teachers/notices/{noticeId}")
    public ResponseDto getNotice(@PathVariable Long noticeId) {
        return ResponseDto.success(noticeService.findById(noticeId));
    }
}
