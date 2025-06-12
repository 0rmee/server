package com.ormee.server.controller;

import com.ormee.server.dto.homework.HomeworkSubmitSaveDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.service.HomeworkSubmitService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class HomeworkSubmitController {
    private final HomeworkSubmitService homeworkSubmitService;

    public HomeworkSubmitController(HomeworkSubmitService homeworkSubmitService) {
        this.homeworkSubmitService = homeworkSubmitService;
    }

    @PostMapping("/student/homeworks/{homeworkId}")
    public ResponseDto createHomeworkSubmit(@PathVariable Long homeworkId, @ModelAttribute HomeworkSubmitSaveDto homeworkSubmitSaveDto, Authentication authentication) throws IOException {
        homeworkSubmitService.create(homeworkId, homeworkSubmitSaveDto, authentication.getName());
        return ResponseDto.success();
    }

    @GetMapping("/teachers/homeworks/{homeworkId}/students")
    public ResponseDto readTeacherHomeworkStudentsList(@PathVariable Long homeworkId, @RequestParam(required = false, defaultValue = "전체") String filter) {
        return switch (filter) {
            case "전체" -> ResponseDto.success(homeworkSubmitService.getStudents(homeworkId));
            case "미제출" -> ResponseDto.success(homeworkSubmitService.getNotSubmittedStudents(homeworkId));
            case "미확인" -> ResponseDto.success(homeworkSubmitService.getNotCheckedStudents(homeworkId));
            default -> throw new CustomException(ExceptionType.FILTER_INVALID_EXCEPTION);
        };
    }

    @GetMapping("/teachers/homeworks/{homeworkId}/students/submit")
    public ResponseDto getSubmitStudents(@PathVariable Long homeworkId) {
        return ResponseDto.success(homeworkSubmitService.getSubmitStudents(homeworkId));
    }

    @GetMapping("/student/homeworks/submit/{homeworkSubmitId}")
    public ResponseDto getHomeworkSubmit(@PathVariable Long homeworkSubmitId, Authentication authentication) {
        return ResponseDto.success(homeworkSubmitService.get(homeworkSubmitId, authentication.getName()));
    }

    // 수정 (피드백 달리기 전까지 수정 가능)
//    @PutMapping("/student/assignment/submit/{assignmentSubmitId}")
//    public ResponseDto updateAssignmentSubmit(@PathVariable Long assignmentSubmitId, @RequestBody AssignmentSubmitSaveDto assignmentSubmitSaveDto) {
//        assignmentSubmitService.update(assignmentSubmitId, assignmentSubmitSaveDto);
//        return ResponseDto.success();
//    }
    // 삭제 (피드백 달리면 삭제 불가능)
//    @DeleteMapping("/student/assignment/submit/{assignmentSubmitId}")
//    public ResponseDto deleteAssignmentSubmit(@PathVariable Long assignmentSubmitId) {
//        assignmentSubmitService.delete(assignmentSubmitId);
//        return ResponseDto.success();
//    }

    // 학생 화면 나오면 필요할 시 추가 작업 (내 제출 과제 목록 등?)
}
