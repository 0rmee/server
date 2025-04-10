package com.ormee.server.controller;

import com.ormee.server.config.jwt.JwtToken;
import com.ormee.server.dto.member.TeacherDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.MemberService;
import com.ormee.server.dto.member.MemberSignInDto;
import com.ormee.server.dto.member.MemberSignUpDto;
import com.ormee.server.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/{role}")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final TeacherService teacherService;

    private boolean isTeacher(String role) {
        return "teacher".equalsIgnoreCase(role);
    }

    @PostMapping("/signup") // 추후 teacher랑 member랑 연결 서비스로직 추가
    public String memberSignUp(@PathVariable String role, @RequestBody @Valid MemberSignUpDto memberRegisterRequest) {
        return memberService.signUp(memberRegisterRequest);
    }

    @PostMapping("/signin") // 추후 teacher랑 member랑 연결 서비스로직 추가
    public JwtToken signIn(@PathVariable String role, @RequestBody MemberSignInDto signInDto) {
        String email = signInDto.getEmail();
        String password = signInDto.getPassword();
        JwtToken jwtToken = memberService.signIn(email, password);
        log.info("request email = {}, password = {}", email, password);
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        return jwtToken;
    }

    @GetMapping("/{teacherId}/profile") // profileDto 나중에 추가
    public ResponseDto teacherProfile(@PathVariable String role, @PathVariable Integer teacherId) {
        if (!isTeacher(role)) {
            throw new IllegalArgumentException("Only teacher role is supported.");
        }
        return ResponseDto.success(teacherService.getTeacherById(teacherId));
    }

    @PutMapping("/{teacherId}/profile") // profileDto 나중에 추가
    public ResponseDto teacherProfileModify(@PathVariable String role, @PathVariable Integer teacherId, @RequestBody TeacherDto teacherDto) {
        if (!isTeacher(role)) {
            throw new IllegalArgumentException("Only teacher role is supported.");
        }
        return ResponseDto.success(teacherService.updateTeacherById(teacherId, teacherDto));
    }

    @GetMapping("/{teacherId}/info") // infoDto 추후 수정
    public ResponseDto teacherInfo(@PathVariable String role, @PathVariable Integer teacherId) {
        if (!isTeacher(role)) {
            throw new IllegalArgumentException("Only teacher role is supported.");
        }
        return ResponseDto.success(teacherService.getTeacherById(teacherId));
    }

    @PutMapping("/{teacherId}/info") // infoDto 추후 수정
    public ResponseDto teacherInfoUpdate(@PathVariable String role, @PathVariable Integer teacherId, @RequestBody TeacherDto teacherDto) {
        if (!isTeacher(role)) {
            throw new IllegalArgumentException("Only teacher role is supported.");
        }
        return ResponseDto.success(teacherService.updateTeacherById(teacherId, teacherDto));
    }

    @PutMapping("/{teacherId}/password/{password}")
    public ResponseDto teacherPasswordUpdate(@PathVariable String role, @PathVariable Integer teacherId, @PathVariable String password) {
        if (!isTeacher(role)) {
            throw new IllegalArgumentException("Only teacher role is supported.");
        }
        return ResponseDto.success(); // 추후 비밀번호 수정 서비스로직 추가
    }

    @PostMapping("/{teacherId}/password/{password}")
    public ResponseDto teacherPasswordCheck(@PathVariable String role, @PathVariable Integer teacherId, @PathVariable String password) {
        if (!isTeacher(role)) {
            throw new IllegalArgumentException("Only teacher role is supported.");
        }
        return ResponseDto.success(); // 추후 비밀번호 확인 서비스로직 추가
    }
}
