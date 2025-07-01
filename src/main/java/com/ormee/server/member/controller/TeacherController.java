package com.ormee.server.member.controller;

import com.ormee.server.member.dto.PasswordDto;
import com.ormee.server.member.dto.SignInDto;
import com.ormee.server.member.dto.TeacherDto;
import com.ormee.server.member.dto.SignUpDto;
import com.ormee.server.global.response.ResponseDto;
import com.ormee.server.member.service.TeacherService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/teachers")
public class TeacherController {
    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping("/signup")
    public ResponseDto teacherSignUp(@RequestBody SignUpDto signUpDto){
        teacherService.signUp(signUpDto);
        return ResponseDto.success();
    }

    @PostMapping("/signin")
    public ResponseDto teacherSignIn(@RequestBody SignInDto signInDto) {
        return ResponseDto.success(teacherService.signIn(signInDto));
    }

    @GetMapping("/profile")
    public ResponseDto teacherProfile(Authentication authentication) {
        return ResponseDto.success(teacherService.getProfile(authentication.getName()));
    }

    @PutMapping("/profile")
    public ResponseDto teacherProfileModify(Authentication authentication, @RequestBody TeacherDto teacherDto) throws IOException {
        teacherService.updateProfile(authentication.getName(), teacherDto);
        return ResponseDto.success();
    }

    @GetMapping("/info")
    public ResponseDto teacherInfo(Authentication authentication) {
        return ResponseDto.success(teacherService.getInfo(authentication.getName()));
    }

    @PutMapping("/info")
    public ResponseDto teacherInfoUpdate(Authentication authentication, @RequestBody TeacherDto teacherDto) {
        teacherService.updateInfo(authentication.getName(), teacherDto);
        return ResponseDto.success();
    }

    @PutMapping("/password")
    public ResponseDto teacherPasswordUpdate(Authentication authentication, @RequestBody PasswordDto passwordDto) {
        teacherService.updatePassword(authentication.getName(), passwordDto);
        return ResponseDto.success();
    }

    @PostMapping("/password")
    public ResponseDto teacherPasswordCheck(Authentication authentication, @RequestBody PasswordDto passwordDto) {
        teacherService.checkPassword(authentication.getName(), passwordDto);
        return ResponseDto.success();
    }
}