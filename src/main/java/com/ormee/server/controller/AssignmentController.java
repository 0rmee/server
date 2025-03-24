package com.ormee.server.controller;

import com.ormee.server.dto.assignment.AssignmentSaveDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.AssignmentService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/teacher/{lectureId}/assignment")
    public ResponseDto createAssignment(@PathVariable UUID lectureId, @RequestBody AssignmentSaveDto assignmentSaveDto) {
        assignmentService.create(lectureId, assignmentSaveDto);
        return ResponseDto.success();
    }

    @GetMapping("/student/{lectureId}/assignment")
    public ResponseDto readAssignmentList(@PathVariable UUID lectureId) {
        return ResponseDto.success(assignmentService.getList(lectureId));
    }

    @GetMapping("/teacher/{lectureId}/assignment")
    public ResponseDto readFeedbackCompletedAssignmentList(@PathVariable UUID lectureId) {
        return ResponseDto.success(assignmentService.getFeedbackCompletedList(lectureId));
    }

    @GetMapping("/teacher/assignment/{assignmentId}")
    public ResponseDto readAssignment(@PathVariable Long assignmentId) {
        return ResponseDto.success(assignmentService.get(assignmentId));
    }

    @PutMapping("/teacher/assignment/{assignmentId}")
    public ResponseDto updateAssignment(@PathVariable Long assignmentId, @RequestBody AssignmentSaveDto assignmentSaveDto) {
        assignmentService.update(assignmentId, assignmentSaveDto);
        return ResponseDto.success();
    }

    @DeleteMapping("/teacher/assignment/{assignmentId}")
    public ResponseDto deleteAssignment(@PathVariable Long assignmentId) {
        assignmentService.delete(assignmentId);
        return ResponseDto.success();
    }
}
