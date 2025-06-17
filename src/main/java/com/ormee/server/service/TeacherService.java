package com.ormee.server.service;

import com.ormee.server.config.jwt.JwtToken;
import com.ormee.server.config.jwt.JwtTokenProvider;
import com.ormee.server.config.jwt.RefreshToken;
import com.ormee.server.dto.member.*;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.Attachment;
import com.ormee.server.model.AttachmentType;
import com.ormee.server.model.member.Member;
import com.ormee.server.model.member.Role;
import com.ormee.server.repository.MemberRepository;
import com.ormee.server.repository.RefreshTokenRepository;
import com.ormee.server.service.attachment.AttachmentService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AttachmentService attachmentService;

    public TeacherService(MemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, AttachmentService attachmentService) {
        this.memberRepository = memberRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.attachmentService = attachmentService;
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

    public void updateProfile(String username, String introduction, MultipartFile file) throws IOException {
        Member teacher = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        if(teacher.getRole() != Role.TEACHER) {
            throw new CustomException(ExceptionType.ACCESS_FORBIDDEN_EXCEPTION);
        }

        teacher.setIntroduction(introduction);
        teacher.setImage(attachmentService.save(AttachmentType.TEACHER_IMAGE, teacher.getId(), file));

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