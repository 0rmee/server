package com.ormee.server.member.controller;

import com.ormee.server.member.service.StudentService;
import com.ormee.server.member.dto.SignInDto;
import com.ormee.server.member.dto.StudentSignUpDto;
import com.ormee.server.global.response.ResponseDto;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/student/signup")
    public ResponseDto signUp(@RequestBody StudentSignUpDto signUpDto) {
        studentService.signUp(signUpDto);
        return ResponseDto.success();
    }

    @PostMapping("/student/signin")
    public ResponseDto signIn(@RequestBody SignInDto signInDto) {
        return ResponseDto.success(studentService.signIn(signInDto));
    }

    // 학생 화면 완성 후 추가 수정

    @DeleteMapping("/student")
    public ResponseDto delete(Authentication authentication) {
        studentService.delete(authentication.getName());
        return ResponseDto.success();
    }
}
