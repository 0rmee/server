package com.ormee.server.controller;

import com.ormee.server.dto.assignment.AssignmentSubmitSaveDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.AssignmentSubmitService;
import org.springframework.web.bind.annotation.*;

@RestController
public class AssignmentSubmitController {
    private final AssignmentSubmitService assignmentSubmitService;

    public AssignmentSubmitController(AssignmentSubmitService assignmentSubmitService) {
        this.assignmentSubmitService = assignmentSubmitService;
    }

    // email -> student principal

    @PostMapping("/student/assignment/{assignmentId}")
    public ResponseDto createAssignmentSubmit(@PathVariable Long assignmentId, @RequestBody AssignmentSubmitSaveDto assignmentSubmitSaveDto, String email) {
        assignmentSubmitService.create(assignmentId, assignmentSubmitSaveDto, email);
        return ResponseDto.success();
    }

    @GetMapping("/teacher/assignment/{assignmentId}")
    public ResponseDto getAssignmentSubmitStudentList(@PathVariable Long assignmentId) {
        return ResponseDto.success(assignmentSubmitService.getStudentList(assignmentId));
    }

    @GetMapping("/student/assignment/submit/{assignmentSubmitId}")
    public ResponseDto getAssignmentSubmit(@PathVariable Long assignmentSubmitId) {
        return ResponseDto.success(assignmentSubmitService.get(assignmentSubmitId));
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
