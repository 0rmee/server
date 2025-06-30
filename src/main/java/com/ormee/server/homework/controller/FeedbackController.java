package com.ormee.server.homework.controller;

import com.ormee.server.homework.service.FeedbackService;
import com.ormee.server.homework.dto.FeedbackSaveDto;
import com.ormee.server.global.response.ResponseDto;
import org.springframework.web.bind.annotation.*;

@RestController
public class FeedbackController {
    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/teachers/homeworks/submissions/{submissionId}")
    public ResponseDto createFeedback(@PathVariable Long submissionId, @RequestBody FeedbackSaveDto feedbackSaveDto) {
        feedbackService.save(submissionId, feedbackSaveDto);
        return ResponseDto.success();
    }

    // 위 로직은 피드백 복수로 달을 수 있는 경우입니다. 복수 불가능시 해당 로직으로 추후 재수정
    @GetMapping("/teachers/homeworks/submissions/{submissionId}")
    public ResponseDto readFeedback(@PathVariable Long submissionId) {
        return ResponseDto.success(feedbackService.get(submissionId));
    }

    @PutMapping("/teachers/homeworks/feedback/{feedbackId}")
    public ResponseDto updateFeedback(@PathVariable Long feedbackId, @RequestBody FeedbackSaveDto feedbackSaveDto) {
        feedbackService.update(feedbackId, feedbackSaveDto);
        return ResponseDto.success();
    }

    @DeleteMapping("/teachers/homeworks/feedback/{feedbackId}")
    public ResponseDto deleteFeedback(@PathVariable Long feedbackId) {
        feedbackService.delete(feedbackId);
        return ResponseDto.success();
    }
}
