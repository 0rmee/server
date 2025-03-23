package com.ormee.server.controller;

import com.ormee.server.dto.feedback.FeedbackSaveDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.FeedbackService;
import org.springframework.web.bind.annotation.*;

@RestController
public class FeedbackController {
    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/teacher/{assignmentSubmitId}/feedback/")
    public ResponseDto createFeedback(@PathVariable Long assignmentSubmitId, @RequestBody FeedbackSaveDto feedbackSaveDto) {
        feedbackService.save(assignmentSubmitId, feedbackSaveDto);
        return ResponseDto.success();
    }

    @GetMapping("/teacher/{assignmentSubmitId}/feedback")
    public ResponseDto readFeedback(@PathVariable Long assignmentSubmitId) {
        return ResponseDto.success(feedbackService.get(assignmentSubmitId));
    }

    @PutMapping("/teacher/feedback/{feedbackId}")
    public ResponseDto updateFeedback(@PathVariable Long feedbackId, @RequestBody FeedbackSaveDto feedbackSaveDto) {
        feedbackService.update(feedbackId, feedbackSaveDto);
        return ResponseDto.success();
    }

    @DeleteMapping("/teacher/feedback/{feedbackId}")
    public ResponseDto deleteFeedback(@PathVariable Long feedbackId) {
        feedbackService.delete(feedbackId);
        return ResponseDto.success();
    }
}
