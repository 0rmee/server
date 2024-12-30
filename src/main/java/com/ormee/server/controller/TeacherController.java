package com.ormee.server.controller;

import com.ormee.server.dto.member.SignInDto;
import com.ormee.server.dto.member.SignUpDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/teacher/users")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @GetMapping("/{code}")
    public ResponseDto teacherInfo(@PathVariable Integer code) {
        return ResponseDto.success(teacherService.getTeacherById(code));
    }

    @PostMapping("/signUp")
    public ResponseDto teacherSignUp(@RequestBody SignUpDto signUpDto){
        teacherService.signUp(signUpDto);
        return ResponseDto.success();
    }

    @PostMapping("/check")
    public ResponseDto teacherCheck(@RequestBody SignInDto signInDto) {
        return ResponseDto.success(teacherService.checkTeacherPassword(signInDto));
    }
}
