package com.ormee.server.member.service;

import com.ormee.server.global.config.jwt.JwtToken;
import com.ormee.server.global.config.jwt.JwtTokenProvider;
import com.ormee.server.global.config.jwt.RefreshToken;
import com.ormee.server.member.domain.DeviceToken;
import com.ormee.server.member.domain.Member;
import com.ormee.server.member.domain.Role;
import com.ormee.server.member.dto.PasswordDto;
import com.ormee.server.member.dto.SignInDto;
import com.ormee.server.member.dto.SignUpDto;
import com.ormee.server.member.dto.TokenDto;
import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.member.repository.DeviceTokenRepository;
import com.ormee.server.member.repository.MemberRepository;
import com.ormee.server.member.repository.RefreshTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public StudentService(MemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository, DeviceTokenRepository deviceTokenRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.deviceTokenRepository = deviceTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void signUp(SignUpDto signUpDto) {
        Member student = Member.builder()
                .username(signUpDto.getUsername())
                .email(signUpDto.getEmail())
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

    public void delete(String username) {
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        memberRepository.delete(student);
    }

    public void updatePassword(String username, PasswordDto passwordDto) {
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        if(student.getRole() != Role.STUDENT) {
            throw new CustomException(ExceptionType.ACCESS_FORBIDDEN_EXCEPTION);
        }

        checkPassword(username, passwordDto);

        student.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        memberRepository.save(student);
    }

    public boolean checkPassword(String username, PasswordDto passwordDto) {
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        if (!passwordEncoder.matches(passwordDto.getPassword(), student.getPassword())) {
            throw new CustomException(ExceptionType.PASSWORD_INVALID_EXCEPTION);
        }

        return true;
    }

    public void updateEmail(String username, SignUpDto signUpDto) {
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        checkEmail(signUpDto);

        student.setEmail(signUpDto.getEmail());
        memberRepository.save(student);
    }

    public boolean checkEmail(SignUpDto signUpDto) {
        if(memberRepository.existsByEmail(signUpDto.getEmail())) {
            throw new CustomException(ExceptionType.EMAIL_ALREADY_EXIST_EXCEPTION);
        }

        return true;
    }

    public boolean checkUsername(SignInDto signInDto) {
        if(memberRepository.existsByUsernameAndRole(signInDto.getUsername(), Role.STUDENT)) {
            throw new CustomException(ExceptionType.USERNAME_ALREADY_EXIST_EXCEPTION);
        }

        return true;
    }

    public void saveOrUpdateDeviceToken(String username, String token) {
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        Optional<DeviceToken> existingToken = deviceTokenRepository.findByMemberIdAndDeviceToken(student.getId(), token);

        DeviceToken deviceToken = existingToken.orElseGet(DeviceToken::new);

        deviceToken.setMemberId(student.getId());
        deviceToken.setDeviceToken(token);

        deviceTokenRepository.save(deviceToken);
    }

    public String getName(String username) {
        Member student = memberRepository.findByUsernameAndRole(username, Role.STUDENT).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        return student.getName();
    }

    public void updateInfo(String username, SignUpDto signUpDto) {
        Member student = memberRepository.findByUsernameAndRole(username, Role.STUDENT).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        if(signUpDto.getName() != null) {
            student.setName(signUpDto.getName());
        }

        if(signUpDto.getEmail() != null) {
            student.setName(signUpDto.getEmail());
        }

        if(signUpDto.getPassword() != null) {
            student.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        }

        memberRepository.save(student);
    }
}
