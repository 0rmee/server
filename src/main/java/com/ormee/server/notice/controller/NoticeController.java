package com.ormee.server.notice.controller;

import com.ormee.server.notice.service.NoticeService;
import com.ormee.server.notice.dto.NoticeSaveDto;
import com.ormee.server.global.response.ResponseDto;
import org.springframework.security.core.Authentication;
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
    public ResponseDto createNotice(@PathVariable Long lectureId, @ModelAttribute NoticeSaveDto noticeSaveDto, Authentication authentication) throws IOException {
        noticeService.saveNotice(lectureId, noticeSaveDto, authentication.getName());
        return ResponseDto.success();
    }

    @GetMapping("/teachers/{lectureId}/notices")
    public ResponseDto getNoticeList(@PathVariable Long lectureId, @RequestParam(defaultValue = "1") int page) {
        return ResponseDto.success(noticeService.findAllByLectureId(lectureId, page - 1));
    }

    @GetMapping("/teachers/{lectureId}/notices/pinned")
    public ResponseDto getPinnedNotices(@PathVariable Long lectureId) {
        return ResponseDto.success(noticeService.getPinnedNotices(lectureId));
    }

    @GetMapping("/teachers/{lectureId}/notices/draft")
    public ResponseDto getDraftNotices(@PathVariable Long lectureId) {
        return ResponseDto.success(noticeService.getDraftNotices(lectureId));
    }

    @GetMapping("/teachers/{lectureId}/notices/load")
    public ResponseDto loadNotices(@PathVariable Long lectureId) {
        return ResponseDto.success(noticeService.loadSavedNotices(lectureId));
    }

    @GetMapping("/teachers/{lectureId}/notices/search")
    public ResponseDto searchNotice(
            @PathVariable Long lectureId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page) {

        return ResponseDto.success(noticeService.findByKeyword(lectureId, keyword, page - 1));
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

    @GetMapping("/teachers/notices/{noticeId}")
    public ResponseDto getNotice(@PathVariable Long noticeId) {
        return ResponseDto.success(noticeService.findById(noticeId));
    }
}
