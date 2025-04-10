package com.ormee.server.controller;

import com.ormee.server.dto.notice.NoticeSaveDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.NoticeService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping
public class NoticeController {
    private final NoticeService noticeService;
    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping("/teachers/{lectureId}/notices")
    public ResponseDto createNotice(@PathVariable UUID lectureId, @ModelAttribute NoticeSaveDto noticeSaveDto) throws IOException {
        noticeService.saveNotice(lectureId, noticeSaveDto);
        return ResponseDto.success();
    }

    @GetMapping("/teachers/{lectureId}/notices/search")
    public ResponseDto searchNotice(@PathVariable UUID lectureId, RequestParam keyword) throws IOException {
        return ResponseDto.success(); // 추후 서비스로직 추가
    }

    @PutMapping("/teachers/notices/{noticeId}")
    public ResponseDto updateNotice(@PathVariable Long noticeId, @RequestBody NoticeSaveDto noticeSaveDto) throws IOException {
        noticeService.modifyNotice(noticeId, noticeSaveDto);
        return ResponseDto.success();
    }

    @PutMapping("/teachers/notices/{noticeId}/pin")
    public ResponseDto updatePinNotice(@PathVariable Long noticeId, @RequestBody NoticeSaveDto noticeSaveDto) throws IOException {
        return ResponseDto.success(); // 추후 서비스 로직 추가
    }

    @PutMapping("/teachers/notices/{noticeId}/unpin")
    public ResponseDto updateUnPinNotice(@PathVariable Long noticeId, @RequestBody NoticeSaveDto noticeSaveDto) throws IOException {
        return ResponseDto.success(); // 추후 서비스 로직 추가
    }

    @DeleteMapping("/teachers/notices/{noticeId}")
    public ResponseDto deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return ResponseDto.success();
    }

    @GetMapping("/teachers/{lectureId}/notices")
    public ResponseDto getNoticeList(@PathVariable UUID lectureId) {
        return ResponseDto.success(noticeService.findAllByLectureId(lectureId));
    }

    @GetMapping("/teachers/notices/{noticeId}")
    public ResponseDto getNotice(@PathVariable Long noticeId) {
        return ResponseDto.success(noticeService.findById(noticeId));
    }
}
