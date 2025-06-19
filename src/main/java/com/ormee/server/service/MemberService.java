package com.ormee.server.service;

import com.ormee.server.dto.member.FindPasswordDto;
import com.ormee.server.dto.member.FindUsernameDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.member.Member;
import com.ormee.server.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String findUsername(FindUsernameDto findUsernameDto) {
        Member member = memberRepository.findByNameAndPhoneNumber(findUsernameDto.getName(), findUsernameDto.getPhoneNumber()).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        return member.getUsername();
    }

    public void findPassword(FindPasswordDto findPasswordDto) {
        Member member = memberRepository.findByUsernameAndPhoneNumber(findPasswordDto.getUsername(), findPasswordDto.getPhoneNumber()).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        member.setPassword(passwordEncoder.encode(findPasswordDto.getNewPassword()));
        member.setLoginFailCount(0L);
        member.setLocked(false);
        memberRepository.save(member);
    }
}
