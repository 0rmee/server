package com.ormee.server.controller;

import com.ormee.server.dto.assignment.AssignmentSaveDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.service.AssignmentService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/teachers/{lectureId}/assignments")
    public ResponseDto createAssignment(@PathVariable Long lectureId, @ModelAttribute AssignmentSaveDto assignmentSaveDto) throws IOException {
        assignmentService.create(lectureId, assignmentSaveDto);
        return ResponseDto.success();
    }

    @GetMapping("/teachers/{lectureId}/assignments")
    public ResponseDto readTeacherAssignmentList(@PathVariable Long lectureId) {
        return ResponseDto.success(assignmentService.getList(lectureId));
    }

    @GetMapping("/teachers/{lectureId}/assignments/drafts")
    public ResponseDto readAssignmentDrafts(@PathVariable Long lectureId) {
        return ResponseDto.success(assignmentService.getDrafts(lectureId));
    }

    @PutMapping("/teachers/assignments/{assignmentId}")
    public ResponseDto updateAssignment(@PathVariable Long assignmentId, @ModelAttribute AssignmentSaveDto assignmentSaveDto) throws IOException {
        assignmentService.update(assignmentId, assignmentSaveDto);
        return ResponseDto.success();
    }

    @DeleteMapping("/teachers/assignments/{assignmentId}")
    public ResponseDto deleteAssignment(@PathVariable Long assignmentId) {
        assignmentService.delete(assignmentId);
        return ResponseDto.success();
    }

    @PostMapping("/teachers/assignments/{assignmentId}/notifications") // @RequestBody StudentList 추가
    public ResponseDto sendAssignmentNotification(@PathVariable Long assignmentId) {
        return ResponseDto.success(); // service logic 추후 추가
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
