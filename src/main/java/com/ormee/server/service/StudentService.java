package com.ormee.server.service;

import com.ormee.server.config.jwt.JwtToken;
import com.ormee.server.config.jwt.JwtTokenProvider;
import com.ormee.server.config.jwt.RefreshToken;
import com.ormee.server.dto.member.SignInDto;
import com.ormee.server.dto.member.StudentDto;
import com.ormee.server.dto.member.StudentSignUpDto;
import com.ormee.server.dto.member.TokenDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.member.Member;
import com.ormee.server.model.member.Role;
import com.ormee.server.repository.MemberRepository;
import com.ormee.server.repository.RefreshTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public StudentService(MemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void signUp(StudentSignUpDto signUpDto) {
        Member student = Member.builder()
                .username(signUpDto.getUsername())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .phoneNumber(signUpDto.getPhoneNumber())
                .name(signUpDto.getName())
                .role(Role.STUDENT)
                .build();
        memberRepository.save(student);
    }

    public TokenDto signIn(SignInDto signInDto) {
        Member student = memberRepository.findByUsername(signInDto.getUsername()).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        if(!passwordEncoder.matches(signInDto.getPassword(), student.getPassword())) {
            throw new CustomException(ExceptionType.PASSWORD_INVALID_EXCEPTION);
        }

        if(student.getRole() != Role.STUDENT) {
            throw new CustomException(ExceptionType.ACCESS_FORBIDDEN_EXCEPTION);
        }

        JwtToken jwtToken = jwtTokenProvider.generateToken(student.getUsername(), List.of("ROLE_STUDENT"));
        String accessToken = jwtToken.getAccessToken();
        String refreshToken = jwtToken.getRefreshToken();

        refreshTokenRepository.save(new RefreshToken(student.getUsername(), refreshToken));

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // student update

    public void delete(String username) {
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        memberRepository.delete(student);
    }
}
