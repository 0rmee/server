package com.ormee.server.homework.controller;

import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.homework.service.HomeworkService;
import com.ormee.server.homework.dto.HomeworkSaveDto;
import com.ormee.server.global.response.ResponseDto;
import com.ormee.server.homework.service.HomeworkSubmitService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController("/teachers")
public class TeacherHomeworkController {

    private final HomeworkService homeworkService;
    private final HomeworkSubmitService homeworkSubmitService;

    public TeacherHomeworkController(HomeworkService homeworkService, HomeworkSubmitService homeworkSubmitService) {
        this.homeworkService = homeworkService;
        this.homeworkSubmitService = homeworkSubmitService;
    }

    @PostMapping("/{lectureId}/homeworks")
    public ResponseDto createHomework(@PathVariable Long lectureId, @ModelAttribute HomeworkSaveDto homeworkSaveDto) throws IOException {
        homeworkService.create(lectureId, homeworkSaveDto);
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

//    @GetMapping("/teachers/{lectureId}/assignment")
//    public ResponseDto readFeedbackCompletedAssignmentList(@PathVariable UUID lectureId) {
//        return ResponseDto.success(assignmentService.getFeedbackCompletedList(lectureId));
//    }

//    @GetMapping("/student/{lectureId}/assignment")
//    public ResponseDto readAssignmentList(@PathVariable UUID lectureId) {
//        return ResponseDto.success(assignmentService.getList(lectureId));
//    }
}
