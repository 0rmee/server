package com.ormee.server.controller;

import com.ormee.server.dto.lecture.LectureRequestDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.LectureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teachers")
public class LectureController {
    // 추후 code->lectureId로 전부 수정

    @Autowired
    private LectureService lectureService;

    @PostMapping("/{teacherId}/lectures")
    public ResponseDto createLecture(@RequestBody LectureRequestDto lectureRequestDto, @PathVariable Integer teacherId) {
        return ResponseDto.success(lectureService.saveLecture(lectureRequestDto, teacherId));
    }

    @PutMapping("/lectures/{lectureId}") // 강의 수정 추후 추가
    public ResponseDto updateLecture(@RequestBody LectureRequestDto lectureRequestDto, @PathVariable Integer lectureId) {
        return ResponseDto.success();
    }

    @DeleteMapping("/lectures/{lectureId}")
    public ResponseDto deleteLecture(@PathVariable Integer lectureId) {
        lectureService.delete(lectureId);
        return ResponseDto.success();
    }

    @GetMapping("/{teacherid}/lectures")
    public ResponseDto getLectureList(@PathVariable Integer teacherId) {
        return ResponseDto.success(lectureService.findAllLectures(teacherId));
    }

    @GetMapping("/lectures/{lectureId}/students")
    public ResponseDto getStudentList(@PathVariable Integer lectureId) {
        return ResponseDto.success(); // 추후 서비스로직 추가
    }

    @PutMapping("/lectures/{lectureId}/students/{studentId}")
    public ResponseDto updateStudentMemo(@PathVariable Integer lectureId, @PathVariable Integer studentId) {
        return ResponseDto.success(); // 추후 서비스로직 추가
    }

    @DeleteMapping("/lectures/{lectureId}/students/{studentId}")
    public ResponseDto deleteStudentMemo(@PathVariable Integer lectureId, @PathVariable Integer studentId) {
        return ResponseDto.success(); // 추후 서비스로직 추가
    }

    // 아래 코드 처분은 어떻게 할까요??

    @GetMapping("/{code}") // 추후 강의홈 uri 방식 정해지면 그때 수정
    public ResponseDto getLecture(@PathVariable Integer code) {
        return ResponseDto.success(lectureService.findLectureByCode(code));
    }

    @PutMapping("/{code}")
    public ResponseDto closeLecture(@PathVariable Integer code) {
        lectureService.close(code);
        return ResponseDto.success();
    }

    @GetMapping("/{code}/validate")
    public ResponseDto validateLecture(@PathVariable Integer code) {
        return ResponseDto.success(lectureService.validate(code));
    }
}
