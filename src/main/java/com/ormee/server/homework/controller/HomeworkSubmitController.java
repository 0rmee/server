package com.ormee.server.homework.controller;

import com.ormee.server.homework.service.HomeworkSubmitService;
import com.ormee.server.homework.dto.HomeworkSubmitSaveDto;
import com.ormee.server.global.response.ResponseDto;
import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class HomeworkSubmitController {
    private final HomeworkSubmitService homeworkSubmitService;

    public HomeworkSubmitController(HomeworkSubmitService homeworkSubmitService) {
        this.homeworkSubmitService = homeworkSubmitService;
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
