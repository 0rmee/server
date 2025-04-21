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
                .phoneNumber2(signUpDto.getPhoneNumber2())
                .email(signUpDto.getEmail())
                .name(signUpDto.getName())
                .nameEng(signUpDto.getNameEng())
                .role(Role.TEACHER)
                .build();
        memberRepository.save(teacher);
    }

    public TokenDto signIn(SignInDto signInDto) {
        Member teacher = memberRepository.findByUsername(signInDto.getUsername())
                .orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        if (!passwordEncoder.matches(signInDto.getPassword(), teacher.getPassword())) {
            throw new CustomException(ExceptionType.PASSWORD_INVALID_EXCEPTION);
        }

        if(teacher.getRole() != Role.TEACHER) {
            throw new CustomException(ExceptionType.ACCESS_FORBIDDEN_EXCEPTION);
        }

        JwtToken jwtToken = jwtTokenProvider.generateToken(teacher.getUsername(), List.of("ROLE_TEACHER"));
        String accessToken = jwtToken.getAccessToken();
        String refreshToken = jwtToken.getRefreshToken();

        refreshTokenRepository.save(new RefreshToken(teacher.getUsername(), refreshToken));

        TokenDto tokenDto = new TokenDto();
        tokenDto.setAccessToken(accessToken);
        tokenDto.setRefreshToken(refreshToken);
        return tokenDto;
    }

    public TeacherDto getProfile(String username) {
        Member teacher = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        return TeacherDto.builder()
                .name(teacher.getName())
                .image(Optional.ofNullable(teacher.getImage())
                        .map(Attachment::getFilePath)
                        .orElse(null))
                .introduction(teacher.getIntroduction())
                .build();
    }

    public void updateProfile(String username, TeacherDto teacherDto, MultipartFile file) throws IOException {
        Member teacher = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        if(teacher.getRole() != Role.TEACHER) {
            throw new CustomException(ExceptionType.ACCESS_FORBIDDEN_EXCEPTION);
        }

        teacher.setIntroduction(teacherDto.getIntroduction());
        teacher.setImage(attachmentService.save(AttachmentType.TEACHER_IMAGE, teacher.getId(), file));

        memberRepository.save(teacher);
    }

    public TeacherDto getInfo(String username) {
        Member teacher = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        return TeacherDto.builder()
                .username(teacher.getUsername())
                .name(teacher.getName())
                .nameEng(teacher.getNameEng())
                .phoneNumber(teacher.getPhoneNumber())
                .phoneNumber2(teacher.getPhoneNumber2())
                .email(teacher.getEmail())
                .build();
    }

    public void updateInfo(String username, TeacherDto teacherDto) {
        Member teacher = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        if(teacher.getRole() != Role.TEACHER) {
            throw new CustomException(ExceptionType.ACCESS_FORBIDDEN_EXCEPTION);
        }

        teacher.setName(teacherDto.getName());
        teacher.setNameEng(teacherDto.getNameEng());
        teacher.setPhoneNumber(teacherDto.getPhoneNumber());
        teacher.setPhoneNumber2(teacherDto.getPhoneNumber2());
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

        teacher.setPassword(passwordEncoder.encode(passwordDto.getPassword()));

        memberRepository.save(teacher);
    }

    public void checkPassword(String username, PasswordDto passwordDto) {
        Member teacher = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        if (!passwordEncoder.matches(passwordDto.getPassword(), teacher.getPassword())) {
            throw new CustomException(ExceptionType.PASSWORD_INVALID_EXCEPTION);
        }
    }
}