package com.ormee.server.controller;

import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.dto.student_lecture.StudentDescriptionRequestDto;
import com.ormee.server.service.StudentLectureService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class StudentLectureController {
    private final StudentLectureService studentLectureService;

    public StudentLectureController(StudentLectureService studentLectureService) {
        this.studentLectureService = studentLectureService;
    }

    @PostMapping("/student/{email}/lecture/{lectureId}")
    public ResponseDto inLecture(@PathVariable String email, @PathVariable UUID lectureId) {
        studentLectureService.in(email, lectureId);
        return ResponseDto.success();
    }

    @DeleteMapping("/student/{email}/lecture/{lectureId}")
    public ResponseDto outLecture(@PathVariable String email, @PathVariable UUID lectureId) {
        studentLectureService.out(email, lectureId);
        return ResponseDto.success();
    }

    @GetMapping("/teacher/lecture/{lectureId}/students")
    public ResponseDto studentsInLecture(@PathVariable UUID lectureId) {
        return ResponseDto.success(studentLectureService.getStudentList(lectureId));
    }

    @PutMapping("/teacher/lecture/{lectureId}/student")
    public ResponseDto describeStudent(@PathVariable UUID lectureId, @RequestBody StudentDescriptionRequestDto studentDescriptionRequestDto) {
        studentLectureService.describe(lectureId, studentDescriptionRequestDto);
        return ResponseDto.success();
    }
}
