package com.ormee.server.member.service;

import com.ormee.server.global.config.jwt.JwtToken;
import com.ormee.server.global.config.jwt.JwtTokenProvider;
import com.ormee.server.global.config.jwt.RefreshToken;
import com.ormee.server.dto.member.*;
import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.attachment.domain.Attachment;
import com.ormee.server.attachment.repository.AttachmentRepository;
import com.ormee.server.member.domain.Member;
import com.ormee.server.member.domain.Role;
import com.ormee.server.member.dto.*;
import com.ormee.server.member.repository.MemberRepository;
import com.ormee.server.member.repository.RefreshTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AttachmentRepository attachmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public TeacherService(MemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository, AttachmentRepository attachmentRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.attachmentRepository = attachmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void signUp(TeacherSignUpDto signUpDto) {
        Member teacher = Member.builder()
                .username(signUpDto.getUsername())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .phoneNumber(signUpDto.getPhoneNumber())
                .email(signUpDto.getEmail())
                .name(signUpDto.getName())
                .nickname(signUpDto.getNickname())
                .loginFailCount(0L)
                .locked(false)
                .role(Role.TEACHER)
                .build();
        memberRepository.save(teacher);
    }

    public TokenDto signIn(SignInDto signInDto) {
        Member teacher = memberRepository.findByUsername(signInDto.getUsername())
                .orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        validateAccountLock(teacher);
        validatePassword(signInDto.getPassword(), teacher);
        validateRole(teacher);

        JwtToken jwtToken = jwtTokenProvider.generateToken(teacher.getUsername(), List.of("ROLE_TEACHER"));
        refreshTokenRepository.save(new RefreshToken(teacher.getUsername(), jwtToken.getRefreshToken()));

        teacher.setLoginFailCount(0L);

        return TokenDto.builder()
                .accessToken(jwtToken.getAccessToken())
                .refreshToken(jwtToken.getRefreshToken())
                .build();
    }

    private void validatePassword(String rawPassword, Member teacher) {
        if (!passwordEncoder.matches(rawPassword, teacher.getPassword())) {
            long failCount = teacher.getLoginFailCount() == null ? 1 : teacher.getLoginFailCount() + 1;
            teacher.setLoginFailCount(failCount);
            memberRepository.save(teacher);

            if (failCount >= 5) {
                teacher.setLocked(true);
                memberRepository.save(teacher);
                throw new CustomException(ExceptionType.ACCOUNT_LOCKED_EXCEPTION);
            }

            throw new CustomException(ExceptionType.PASSWORD_INVALID_EXCEPTION);
        }
    }

    private void validateAccountLock(Member teacher) {
        if (Boolean.TRUE.equals(teacher.getLocked()) || (teacher.getLoginFailCount() != null && teacher.getLoginFailCount() >= 5)) {
            teacher.setLocked(true);
            memberRepository.save(teacher);
            throw new CustomException(ExceptionType.ACCOUNT_LOCKED_EXCEPTION);
        }
    }

    private void validateRole(Member teacher) {
        if (teacher.getRole() != Role.TEACHER) {
            throw new CustomException(ExceptionType.ACCESS_FORBIDDEN_EXCEPTION);
        }
    }

    public TeacherDto getProfile(String username) {
        Member teacher = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        return TeacherDto.builder()
                .nickname(teacher.getNickname())
                .image(Optional.ofNullable(teacher.getImage())
                        .map(Attachment::getFilePath)
                        .orElse(null))
                .introduction(teacher.getIntroduction())
                .build();
    }

    public void updateProfile(String username, TeacherDto teacherDto) {
        Member teacher = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        if(teacher.getRole() != Role.TEACHER) {
            throw new CustomException(ExceptionType.ACCESS_FORBIDDEN_EXCEPTION);
        }

        teacher.setIntroduction(teacherDto.getIntroduction());

        if(teacherDto.getFileId() != null) {
            Attachment attachment = attachmentRepository.findById(teacherDto.getFileId()).orElseThrow(() -> new CustomException(ExceptionType.ATTACHMENT_NOT_FOUND_EXCEPTION));
            teacher.setImage(attachment);
        }

        memberRepository.save(teacher);
    }

    public TeacherDto getInfo(String username) {
        Member teacher = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        return TeacherDto.builder()
                .username(teacher.getUsername())
                .name(teacher.getName())
                .nickname(teacher.getNickname())
                .phoneNumber(teacher.getPhoneNumber())
                .email(teacher.getEmail())
                .build();
    }

    public void updateInfo(String username, TeacherDto teacherDto) {
        Member teacher = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        if(teacher.getRole() != Role.TEACHER) {
            throw new CustomException(ExceptionType.ACCESS_FORBIDDEN_EXCEPTION);
        }

        teacher.setName(teacherDto.getName());
        teacher.setNickname(teacherDto.getNickname());
        teacher.setPhoneNumber(teacherDto.getPhoneNumber());
        teacher.setEmail(teacher.getEmail());

        memberRepository.save(teacher);
    }

    public void updatePassword(String username, PasswordDto passwordDto) {
        Member teacher = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        if(teacher.getRole() != Role.TEACHER) {
            throw new CustomException(ExceptionType.ACCESS_FORBIDDEN_EXCEPTION);
        }

        if (!passwordEncoder.matches(passwordDto.getPassword(), teacher.getPassword())) {
            throw new CustomException(ExceptionType.PASSWORD_INVALID_EXCEPTION);
        }

        teacher.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));

        memberRepository.save(teacher);
    }

    public void checkPassword(String username, PasswordDto passwordDto) {
        Member teacher = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        if (!passwordEncoder.matches(passwordDto.getPassword(), teacher.getPassword())) {
            throw new CustomException(ExceptionType.PASSWORD_INVALID_EXCEPTION);
        }
    }
}