package com.ormee.server.controller;

import com.ormee.server.config.jwt.JwtToken;
import com.ormee.server.service.MemberService;
import com.ormee.server.dto.member.MemberSignInDto;
import com.ormee.server.dto.member.MemberSignUpDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signUp")
    public String memberSignUp(@RequestBody @Valid MemberSignUpDto memberRegisterRequest) {
        return memberService.signUp(memberRegisterRequest);
    }

    @PostMapping("/signIn")
    public JwtToken signIn(@RequestBody MemberSignInDto signInDto) {
        String email = signInDto.getEmail();
        String password = signInDto.getPassword();
        JwtToken jwtToken = memberService.signIn(email, password);
        log.info("request email = {}, password = {}", email, password);
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        return jwtToken;
    }
}
