package com.ormee.server.controller;

import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.dto.student.StudentSignInDto;
import com.ormee.server.dto.student.StudentInfoDto;
import com.ormee.server.service.StudentService;
import org.springframework.web.bind.annotation.*;

@RestController
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/student/signup")
    public ResponseDto signUp(@RequestBody StudentInfoDto signUpDto) {
        return ResponseDto.success(studentService.signUp(signUpDto));
    }

    @PostMapping("/student/signin")
    public ResponseDto signIn(@RequestBody StudentSignInDto signInDto) {
        return ResponseDto.success(studentService.signIn(signInDto));
    }

    @GetMapping("/student/{email}")
    public ResponseDto studentInfo(@PathVariable String email) {
        return ResponseDto.success(studentService.getStudentInfo(email));
    }

    @DeleteMapping("/student/{email}")
    public ResponseDto delete(@PathVariable String email) {
        studentService.delete(email);
        return ResponseDto.success();
    }
}
