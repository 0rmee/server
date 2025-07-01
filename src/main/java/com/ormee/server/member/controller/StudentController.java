package com.ormee.server.member.controller;

import com.ormee.server.member.dto.SignUpDto;
import com.ormee.server.member.service.StudentService;
import com.ormee.server.member.dto.SignInDto;
import com.ormee.server.global.response.ResponseDto;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/signup")
    public ResponseDto signUp(@RequestBody SignUpDto signUpDto) {
        studentService.signUp(signUpDto);
        return ResponseDto.success();
    }

    @PostMapping("/signin")
    public ResponseDto signIn(@RequestBody SignInDto signInDto) {
        return ResponseDto.success(studentService.signIn(signInDto));
    }

    @DeleteMapping
    public ResponseDto delete(Authentication authentication) {
        studentService.delete(authentication.getName());
        return ResponseDto.success();
    }
}
