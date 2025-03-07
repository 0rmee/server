package com.ormee.server.controller;

import com.ormee.server.dto.notice.NoticeSaveDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.model.Notice;
import com.ormee.server.service.NoticeService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/notices")
public class NoticeController {
    private final NoticeService noticeService;
    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping("/teacher/{lectureId}")
    public ResponseDto createNotice(@PathVariable UUID lectureId, @RequestBody NoticeSaveDto noticeSaveDto) {
        noticeService.saveNotice(lectureId, noticeSaveDto);
        return ResponseDto.success();
    }

    @PutMapping("/teacher/{noticeId}")
    public ResponseDto updateNotice(@PathVariable Long noticeId, @RequestBody NoticeSaveDto noticeSaveDto) {
        noticeService.modifyNotice(noticeId, noticeSaveDto);
        return ResponseDto.success();
    }

    @DeleteMapping("/teacher/{noticeId}")
    public ResponseDto deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return ResponseDto.success();
    }

    @GetMapping("/{lectureId}")
    public ResponseDto getNotice(@PathVariable UUID lectureId) {
        return ResponseDto.success(noticeService.findAllByLectureId(lectureId));
    }
}
