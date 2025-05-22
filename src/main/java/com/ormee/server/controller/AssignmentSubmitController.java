package com.ormee.server.controller;

import com.ormee.server.dto.assignment.AssignmentSubmitSaveDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.service.AssignmentSubmitService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class AssignmentSubmitController {
    private final AssignmentSubmitService assignmentSubmitService;

    public AssignmentSubmitController(AssignmentSubmitService assignmentSubmitService) {
        this.assignmentSubmitService = assignmentSubmitService;
    }

    @PostMapping("/student/assignment/{assignmentId}")
    public ResponseDto createAssignmentSubmit(@PathVariable Long assignmentId, @ModelAttribute AssignmentSubmitSaveDto assignmentSubmitSaveDto, Authentication authentication) throws IOException {
        assignmentSubmitService.create(assignmentId, assignmentSubmitSaveDto, authentication.getName());
        return ResponseDto.success();
    }

    @GetMapping("/teachers/assignments/{assignmentId}/students")
    public ResponseDto readTeacherAssignmentStudentsList(@PathVariable Long assignmentId, @RequestParam(required = false, defaultValue = "전체") String filter) {
        return switch (filter) {
            case "전체" -> ResponseDto.success(assignmentSubmitService.getStudents(assignmentId));
            case "미제출" -> ResponseDto.success(assignmentSubmitService.getNotSubmittedStudents(assignmentId));
            case "미확인" -> ResponseDto.success(assignmentSubmitService.getNotCheckedStudents(assignmentId));
            default -> throw new CustomException(ExceptionType.FILTER_INVALID_EXCEPTION);
        };
    }

    @GetMapping("/student/assignment/submit/{assignmentSubmitId}")
    public ResponseDto getAssignmentSubmit(@PathVariable Long assignmentSubmitId, Authentication authentication) {
        return ResponseDto.success(assignmentSubmitService.get(assignmentSubmitId, authentication.getName()));
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
