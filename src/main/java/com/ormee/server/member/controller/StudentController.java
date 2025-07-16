package com.ormee.server.member.controller;

import com.ormee.server.member.dto.DeviceTokenDto;
import com.ormee.server.member.dto.PasswordDto;
import com.ormee.server.member.dto.SignUpDto;
import com.ormee.server.member.service.StudentService;
import com.ormee.server.member.dto.SignInDto;
import com.ormee.server.global.response.ResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentController {
    @Value("${app.version}")
    private String appVersion;
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

    @PutMapping("/password")
    public ResponseDto updatePassword(Authentication authentication, @RequestBody PasswordDto passwordDto) {
        studentService.updatePassword(authentication.getName(), passwordDto);
        return ResponseDto.success();
    }

    @PostMapping("/password")
    public ResponseDto checkPassword(Authentication authentication, @RequestBody PasswordDto passwordDto) {
        return ResponseDto.success(studentService.checkPassword(authentication.getName(), passwordDto));
    }

    @PutMapping("/email")
    public ResponseDto updateEmail(Authentication authentication, @RequestBody SignUpDto signUpDto) {
        studentService.updateEmail(authentication.getName(), signUpDto);
        return ResponseDto.success();
    }

    @PostMapping("/email")
    public ResponseDto checkEmail(@RequestBody SignUpDto signUpDto) {
        return ResponseDto.success(studentService.checkEmail(signUpDto));
    }

    @GetMapping("/version")
    public ResponseDto versionCheck() {
        return ResponseDto.success(appVersion);
    }

    @PostMapping("/device")
    public ResponseDto registerDeviceToken(@RequestBody DeviceTokenDto deviceTokenDto, Authentication authentication) {
        studentService.saveOrUpdateDeviceToken(authentication.getName(), deviceTokenDto.getDeviceToken());
        return ResponseDto.success();
    }
}
