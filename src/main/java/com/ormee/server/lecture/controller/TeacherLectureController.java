package com.ormee.server.lecture.controller;

import com.ormee.server.lecture.dto.StudentDescriptionRequestDto;
import com.ormee.server.lecture.service.LectureService;
import com.ormee.server.lecture.dto.LectureRequestDto;
import com.ormee.server.global.response.ResponseDto;
import com.ormee.server.lecture.service.StudentLectureService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teachers/lectures")
public class TeacherLectureController {

    private final LectureService lectureService;
    private final StudentLectureService studentLectureService;

    public TeacherLectureController(LectureService lectureService, StudentLectureService studentLectureService) {
        this.lectureService = lectureService;
        this.studentLectureService = studentLectureService;
    }

    @PostMapping
    public ResponseDto createLecture(@RequestBody LectureRequestDto lectureRequestDto, Authentication authentication) {
        return ResponseDto.success(lectureService.save(lectureRequestDto, authentication.getName()));
    }

    @PutMapping("/{lectureId}")
    public ResponseDto updateLecture(@RequestBody LectureRequestDto lectureRequestDto, @PathVariable Long lectureId, Authentication authentication) {
        lectureService.update(lectureRequestDto, lectureId, authentication.getName());
        return ResponseDto.success();
    }

    @DeleteMapping("/{lectureId}")
    public ResponseDto deleteLecture(@PathVariable Long lectureId, Authentication authentication) {
        lectureService.delete(lectureId, authentication.getName());
        return ResponseDto.success();
    }

    @GetMapping("/load")
    public ResponseDto getAllLectures(Authentication authentication) {
        return ResponseDto.success(lectureService.getAllLectures(authentication.getName()));
    }

    @GetMapping
    public ResponseDto getLectureList(Authentication authentication) {
        return ResponseDto.success(lectureService.findAllLectures(authentication.getName()));
    }

    @GetMapping("/{lectureId}")
    public ResponseDto getLecture(Authentication authentication, @PathVariable Long lectureId) {
        return ResponseDto.success(lectureService.getLecture(lectureId, authentication.getName()));
    }

    @PostMapping("/{lectureId}/collaborators")
    public ResponseDto addCollaborator(@PathVariable Long lectureId, @RequestParam("username") String username) {
        lectureService.addCollaborator(lectureId, username);
        return ResponseDto.success();
    }

    @DeleteMapping("/{lectureId}/collaborators")
    public ResponseDto removeCollaborator(@PathVariable Long lectureId, @RequestParam("username") String username) {
        lectureService.removeCollaborator(lectureId, username);
        return ResponseDto.success();
    }

    @GetMapping("/{lectureId}/students")
    public ResponseDto studentsInLecture(@PathVariable Long lectureId, @RequestParam(required = false, defaultValue = "이름순") String filter, @RequestParam(defaultValue = "1") int page) {
        return ResponseDto.success(studentLectureService.findAllStudents(lectureId, filter, page - 1));
    }

    @PutMapping("/students")
    public ResponseDto describeStudent(@RequestBody StudentDescriptionRequestDto studentDescriptionRequestDto) {
        studentLectureService.updateDescription(studentDescriptionRequestDto);
        return ResponseDto.success();
    }

    @PutMapping("/students/{studentLectureId}/block")
    public ResponseDto blockStudent(@PathVariable Long studentLectureId) {
        studentLectureService.block(studentLectureId, true);
        return ResponseDto.success();
    }

    @PutMapping("/students/{studentLectureId}/unlock")
    public ResponseDto unlockStudent(@PathVariable Long studentLectureId) {
        studentLectureService.block(studentLectureId, false);
        return ResponseDto.success();
    }

    @GetMapping("/{lectureId}/students/block")
    public ResponseDto getBlockedStudents(@PathVariable Long lectureId) {
        return ResponseDto.success(studentLectureService.findBlockedStudentsByLecture(lectureId));
    }
}
