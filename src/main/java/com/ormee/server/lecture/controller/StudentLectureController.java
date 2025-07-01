package com.ormee.server.lecture.controller;

import com.ormee.server.global.response.ResponseDto;
import com.ormee.server.lecture.service.LectureService;
import com.ormee.server.lecture.service.StudentLectureService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students/lectures")
public class StudentLectureController {
    private final StudentLectureService studentLectureService;
    private final LectureService lectureService;

    public StudentLectureController(StudentLectureService studentLectureService, LectureService lectureService) {
        this.studentLectureService = studentLectureService;
        this.lectureService = lectureService;
    }

    @GetMapping
    public ResponseDto getLectures(Authentication authentication) {
        return ResponseDto.success();
    }

    @GetMapping("/history")
    public ResponseDto getMyLectures(Authentication authentication) {
        return ResponseDto.success();
    }

    @GetMapping("/{lectureId}")
    public ResponseDto getLecture(@PathVariable Long lectureId) {
        return ResponseDto.success();
    }

    @PostMapping("/{lectureId}")
    public ResponseDto inLecture(@PathVariable Long lectureId, Authentication authentication) {
        studentLectureService.in(lectureId, authentication.getName());
        return ResponseDto.success();
    }

    @DeleteMapping("/{lectureId}")
    public ResponseDto outLecture(@PathVariable Long lectureId, Authentication authentication) {
        studentLectureService.out(lectureId, authentication.getName());
        return ResponseDto.success();
    }
}
