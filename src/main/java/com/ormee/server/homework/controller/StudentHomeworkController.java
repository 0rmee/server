package com.ormee.server.homework.controller;

import com.ormee.server.global.response.ResponseDto;
import com.ormee.server.homework.dto.HomeworkSubmitSaveDto;
import com.ormee.server.homework.service.FeedbackService;
import com.ormee.server.homework.service.HomeworkService;
import com.ormee.server.homework.service.HomeworkSubmitService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/students")
public class StudentHomeworkController {

    private final HomeworkService homeworkService;
    private final HomeworkSubmitService homeworkSubmitService;
    private final FeedbackService feedbackService;

    public StudentHomeworkController(HomeworkService homeworkService, HomeworkSubmitService homeworkSubmitService, FeedbackService feedbackService) {
        this.homeworkService = homeworkService;
        this.homeworkSubmitService = homeworkSubmitService;
        this.feedbackService = feedbackService;
    }

    @GetMapping("/lectures/{lectureId}/homeworks")
    public ResponseDto getHomeworks(@PathVariable Long lectureId, Authentication authentication) {
        return ResponseDto.success(homeworkService.getHomeworks(lectureId, authentication.getName()));
    }

    @GetMapping("/homeworks/{homeworkId}")
    public ResponseDto getHomework(@PathVariable Long homeworkId, Authentication authentication) {
        return ResponseDto.success(homeworkService.getHomework(homeworkId, authentication.getName()));
    }

    @PostMapping("/homeworks/{homeworkId}")
    public ResponseDto submitHomework(@PathVariable Long homeworkId, @RequestBody HomeworkSubmitSaveDto homeworkSubmitSaveDto, Authentication authentication) {
        homeworkSubmitService.submit(homeworkId, homeworkSubmitSaveDto, authentication.getName());
        return ResponseDto.success();
    }

    @GetMapping("/homeworks/submit/{homeworkSubmitId}")
    public ResponseDto readHomeworkSubmit(@PathVariable Long homeworkSubmitId, Authentication authentication) {
        return ResponseDto.success(homeworkSubmitService.get(homeworkSubmitId, authentication.getName()));
    }

    @GetMapping("/homeworks/{homeworkId}/submissions")
    public ResponseDto readHomeworkSubmitByHomework(@PathVariable Long homeworkId, Authentication authentication) {
        return ResponseDto.success(homeworkSubmitService.findByStudentAndHomework(homeworkId, authentication.getName()));
    }

    @GetMapping("/homeworks/submissions/{submissionId}/feedback")
    public ResponseDto readFeedback(@PathVariable Long submissionId) {
        return ResponseDto.success(feedbackService.get(submissionId));
    }
}
