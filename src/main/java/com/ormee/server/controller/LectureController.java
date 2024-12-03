package com.ormee.server.controller;

import com.ormee.server.dto.LectureRequestDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.LectureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lectures")
public class LectureController {

    @Autowired
    private LectureService lectureService;

    @PostMapping("/{teacherCode}")
    public ResponseDto createLecture(@RequestBody LectureRequestDto lectureRequestDto, @PathVariable Integer teacherCode) {
        return ResponseDto.success(lectureService.saveLecture(lectureRequestDto, teacherCode));
    }

    @GetMapping("/{code}")
    public ResponseDto getLecture(@PathVariable Integer code) {
        return ResponseDto.success(lectureService.findLectureByCode(code));
    }

    @GetMapping("teacher/{teacherCode}")
    public ResponseDto getLectureList(@PathVariable Integer teacherCode) {
        return ResponseDto.success(lectureService.findAllLectures(teacherCode));
    }
}
