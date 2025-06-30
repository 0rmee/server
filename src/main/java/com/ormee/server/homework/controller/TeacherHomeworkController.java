package com.ormee.server.homework.controller;

import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.homework.dto.FeedbackSaveDto;
import com.ormee.server.homework.service.FeedbackService;
import com.ormee.server.homework.service.HomeworkService;
import com.ormee.server.homework.dto.HomeworkSaveDto;
import com.ormee.server.global.response.ResponseDto;
import com.ormee.server.homework.service.HomeworkSubmitService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/teachers")
public class TeacherHomeworkController {

    private final HomeworkService homeworkService;
    private final HomeworkSubmitService homeworkSubmitService;
    private final FeedbackService feedbackService;

    public TeacherHomeworkController(HomeworkService homeworkService, HomeworkSubmitService homeworkSubmitService, FeedbackService feedbackService) {
        this.homeworkService = homeworkService;
        this.homeworkSubmitService = homeworkSubmitService;
        this.feedbackService = feedbackService;
    }

    @PostMapping("/{lectureId}/homeworks")
    public ResponseDto createHomework(@PathVariable Long lectureId, @ModelAttribute HomeworkSaveDto homeworkSaveDto, Authentication authentication) throws IOException {
        homeworkService.create(lectureId, homeworkSaveDto, authentication.getName());
        return ResponseDto.success();
    }

    @GetMapping("/{lectureId}/homeworks")
    public ResponseDto readTeacherHomeworkList(@PathVariable Long lectureId) {
        return ResponseDto.success(homeworkService.getList(lectureId));
    }

    @GetMapping("/{lectureId}/homeworks/drafts")
    public ResponseDto readHomeworkDrafts(@PathVariable Long lectureId) {
        return ResponseDto.success(homeworkService.getDrafts(lectureId));
    }

    @GetMapping("/{lectureId}/homeworks/load")
    public ResponseDto loadHomeworks(@PathVariable Long lectureId) {
        return ResponseDto.success(homeworkService.loadSavedHomeworks(lectureId));
    }

    @GetMapping("/homeworks/{homeworkId}")
    public ResponseDto readHomework(@PathVariable Long homeworkId) {
        return ResponseDto.success(homeworkService.read(homeworkId));
    }

    @PutMapping("/homeworks/{homeworkId}")
    public ResponseDto updateHomework(@PathVariable Long homeworkId, @ModelAttribute HomeworkSaveDto homeworkSaveDto) throws IOException {
        homeworkService.update(homeworkId, homeworkSaveDto);
        return ResponseDto.success();
    }

    @DeleteMapping("/homeworks/{homeworkId}")
    public ResponseDto deleteHomework(@PathVariable Long homeworkId) {
        homeworkService.delete(homeworkId);
        return ResponseDto.success();
    }

    @PostMapping("/homeworks/{homeworkId}/notifications") // @RequestBody StudentList 추가
    public ResponseDto sendHomeworkNotification(@PathVariable Long homeworkId) {
        return ResponseDto.success(); // service logic 추후 추가
    }

    @GetMapping("/homeworks/{homeworkId}/students")
    public ResponseDto readTeacherHomeworkStudentsList(@PathVariable Long homeworkId, @RequestParam(required = false, defaultValue = "전체") String filter) {
        return switch (filter) {
            case "전체" -> ResponseDto.success(homeworkSubmitService.getStudents(homeworkId));
            case "미제출" -> ResponseDto.success(homeworkSubmitService.getNotSubmittedStudents(homeworkId));
            case "미확인" -> ResponseDto.success(homeworkSubmitService.getNotCheckedStudents(homeworkId));
            default -> throw new CustomException(ExceptionType.FILTER_INVALID_EXCEPTION);
        };
    }

    @GetMapping("/homeworks/{homeworkId}/students/submit")
    public ResponseDto getSubmitStudents(@PathVariable Long homeworkId) {
        return ResponseDto.success(homeworkSubmitService.getSubmitStudents(homeworkId));
    }

    @PostMapping("/homeworks/submissions/{submissionId}")
    public ResponseDto createFeedback(@PathVariable Long submissionId, @RequestBody FeedbackSaveDto feedbackSaveDto) {
        feedbackService.save(submissionId, feedbackSaveDto);
        return ResponseDto.success();
    }

    // 위 로직은 피드백 복수로 달을 수 있는 경우입니다. 복수 불가능시 해당 로직으로 추후 재수정
    @GetMapping("/homeworks/submissions/{submissionId}")
    public ResponseDto readFeedback(@PathVariable Long submissionId) {
        return ResponseDto.success(feedbackService.get(submissionId));
    }

    @PutMapping("/homeworks/feedback/{feedbackId}")
    public ResponseDto updateFeedback(@PathVariable Long feedbackId, @RequestBody FeedbackSaveDto feedbackSaveDto) {
        feedbackService.update(feedbackId, feedbackSaveDto);
        return ResponseDto.success();
    }

    @DeleteMapping("/homeworks/feedback/{feedbackId}")
    public ResponseDto deleteFeedback(@PathVariable Long feedbackId) {
        feedbackService.delete(feedbackId);
        return ResponseDto.success();
    }
}
