package com.ormee.server.controller;

import com.ormee.server.dto.member.SignInDto;
import com.ormee.server.dto.member.SignUpDto;
import com.ormee.server.dto.member.TeacherDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.model.Teacher;
import com.ormee.server.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @PostMapping("/signup")
    public ResponseDto teacherSignUp(@RequestBody SignUpDto signUpDto){
        teacherService.signUp(signUpDto);
        return ResponseDto.success();
    }

    @PostMapping("/signin")
    public ResponseDto teacherSignIn(@RequestBody SignInDto signInDto) {
        return ResponseDto.success(teacherService.checkTeacherPassword(signInDto));
    }

    @GetMapping("/{teacherId}/profile") // profileDto 나중에 추가
    public ResponseDto teacherProfile(@PathVariable Integer teacherId) {
        return ResponseDto.success(teacherService.getTeacherById(teacherId));
    }

    @PutMapping("/{teacherId}/profile") // profileDto 나중에 추가
    public ResponseDto teacherProfileModify(@PathVariable Integer teacherId, @RequestBody TeacherDto teacherDto) {
        return ResponseDto.success(teacherService.updateTeacherById(teacherId, teacherDto));
    }

    @GetMapping("/{teacherId}/info") // infoDto 추후 수정
    public ResponseDto teacherInfo(@PathVariable Integer teacherId) {
        return ResponseDto.success(teacherService.getTeacherById(teacherId));
    }

    @PutMapping("/{teacherId}/info") // infoDto 추후 수정
    public ResponseDto teacherInfoUpdate(@PathVariable Integer teacherId, @RequestBody TeacherDto teacherDto) {
        return ResponseDto.success(teacherService.updateTeacherById(teacherId, teacherDto));
    }

    @PutMapping("/{teacherId}/password/{password}")
    public ResponseDto teacherPasswordUpdate(@PathVariable Integer teacherId, @PathVariable String password) {
        return ResponseDto.success(); // 추후 비밀번호 수정 서비스로직 추가
    }

    @PostMapping("/{teacherId}/password/{password}")
    public ResponseDto teacherPasswordCheck(@PathVariable Integer teacherId, @PathVariable String password) {
        return ResponseDto.success(); // 추후 비밀번호 확인 서비스로직 추가
    }
}