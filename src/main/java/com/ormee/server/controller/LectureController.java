package com.ormee.server.controller;

import com.ormee.server.dto.lecture.LectureRequestDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.LectureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teachers")
public class LectureController {

    @Autowired
    private LectureService lectureService;

    @PostMapping("/lectures")
    public ResponseDto createLecture(@RequestBody LectureRequestDto lectureRequestDto, Authentication authentication) {
        lectureService.save(lectureRequestDto, authentication.getName());
        return ResponseDto.success();
    }

    @PutMapping("/lectures/{lectureId}") // 강의 수정 추후 추가
    public ResponseDto updateLecture(@RequestBody LectureRequestDto lectureRequestDto, @PathVariable Long lectureId, Authentication authentication) {
        lectureService.update(lectureRequestDto, lectureId, authentication.getName());
        return ResponseDto.success();
    }

    @DeleteMapping("/lectures/{lectureId}")
    public ResponseDto deleteLecture(@PathVariable Long lectureId, Authentication authentication) {
        lectureService.delete(lectureId, authentication.getName());
        return ResponseDto.success();
    }

    @GetMapping("/lectures")
    public ResponseDto getLectureList(Authentication authentication) {
        return ResponseDto.success(lectureService.findAllLectures(authentication.getName()));
    }

//    @GetMapping("/{code}") // 추후 강의홈 uri 방식 정해지면 그때 수정
//    public ResponseDto getLecture(@PathVariable Integer code) {
//        return ResponseDto.success(lectureService.findLectureByCode(code));
//    }
//
//    @PutMapping("/{code}")
//    public ResponseDto closeLecture(@PathVariable Integer code) {
//        lectureService.close(code);
//        return ResponseDto.success();
//    }
//
//    @GetMapping("/{code}/validate")
//    public ResponseDto validateLecture(@PathVariable Integer code) {
//        return ResponseDto.success(lectureService.validate(code));
//    }
}
