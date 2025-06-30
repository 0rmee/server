package com.ormee.server.homework.controller;

import com.ormee.server.homework.service.HomeworkService;
import com.ormee.server.homework.dto.HomeworkSaveDto;
import com.ormee.server.global.response.ResponseDto;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class HomeworkController {

    private final HomeworkService homeworkService;

    public HomeworkController(HomeworkService homeworkService) {
        this.homeworkService = homeworkService;
    }

    @PostMapping("/teachers/{lectureId}/homeworks")
    public ResponseDto createHomework(@PathVariable Long lectureId, @ModelAttribute HomeworkSaveDto homeworkSaveDto) throws IOException {
        homeworkService.create(lectureId, homeworkSaveDto);
        return ResponseDto.success();
    }

    @GetMapping("/teachers/{lectureId}/homeworks")
    public ResponseDto readTeacherHomeworkList(@PathVariable Long lectureId) {
        return ResponseDto.success(homeworkService.getList(lectureId));
    }

    @GetMapping("/teachers/{lectureId}/homeworks/drafts")
    public ResponseDto readHomeworkDrafts(@PathVariable Long lectureId) {
        return ResponseDto.success(homeworkService.getDrafts(lectureId));
    }

    @GetMapping("/teachers/{lectureId}/homeworks/load")
    public ResponseDto loadHomeworks(@PathVariable Long lectureId) {
        return ResponseDto.success(homeworkService.loadSavedHomeworks(lectureId));
    }

    @GetMapping("/teachers/homeworks/{homeworkId}")
    public ResponseDto readHomework(@PathVariable Long homeworkId) {
        return ResponseDto.success(homeworkService.read(homeworkId));
    }

    @PutMapping("/teachers/homeworks/{homeworkId}")
    public ResponseDto updateHomework(@PathVariable Long homeworkId, @ModelAttribute HomeworkSaveDto homeworkSaveDto) throws IOException {
        homeworkService.update(homeworkId, homeworkSaveDto);
        return ResponseDto.success();
    }

    @DeleteMapping("/teachers/homeworks/{homeworkId}")
    public ResponseDto deleteHomework(@PathVariable Long homeworkId) {
        homeworkService.delete(homeworkId);
        return ResponseDto.success();
    }

    @PostMapping("/teachers/homeworks/{homeworkId}/notifications") // @RequestBody StudentList 추가
    public ResponseDto sendHomeworkNotification(@PathVariable Long homeworkId) {
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
