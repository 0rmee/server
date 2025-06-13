package com.ormee.server.controller;

import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.dto.student_lecture.StudentDescriptionRequestDto;
import com.ormee.server.service.StudentLectureService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class StudentLectureController {
    private final StudentLectureService studentLectureService;

    public StudentLectureController(StudentLectureService studentLectureService) {
        this.studentLectureService = studentLectureService;
    }

    @PostMapping("/student/lecture/{lectureId}")
    public ResponseDto inLecture(@PathVariable Long lectureId, Authentication authentication) {
        studentLectureService.in(lectureId, authentication.getName());
        return ResponseDto.success();
    }

    @DeleteMapping("/student/lecture/{lectureId}")
    public ResponseDto outLecture(@PathVariable Long lectureId, Authentication authentication) {
        studentLectureService.out(lectureId, authentication.getName());
        return ResponseDto.success();
    }

    @GetMapping("/teachers/lectures/{lectureId}/students")
    public ResponseDto studentsInLecture(@PathVariable Long lectureId) {
        return ResponseDto.success(studentLectureService.findAllStudents(lectureId));
    }

    @PutMapping("/teachers/lectures/students")
    public ResponseDto describeStudent(@RequestBody StudentDescriptionRequestDto studentDescriptionRequestDto) {
        studentLectureService.updateDescription(studentDescriptionRequestDto);
        return ResponseDto.success();
    }

    @DeleteMapping("/teachers/lectures/students/{studentLectureId}")
    public ResponseDto outStudent(@PathVariable Long studentLectureId) {
        studentLectureService.delete(studentLectureId);
        return ResponseDto.success();
    }
}
