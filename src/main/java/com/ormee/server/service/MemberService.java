package com.ormee.server.service;

import com.ormee.server.config.jwt.JwtToken;
import com.ormee.server.config.jwt.JwtTokenProvider;
import com.ormee.server.dto.member.MemberSignUpDto;
import com.ormee.server.model.Member;
import com.ormee.server.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public String signUp(MemberSignUpDto memberSignUpDto) {
        if (memberRepository.findByLoginId(memberSignUpDto.getLoginId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 로그인 ID입니다.");
        }

        String encodedPassword = passwordEncoder.encode(memberSignUpDto.getPassword());
        Member member = memberSignUpDto.toEntity(encodedPassword);
        memberRepository.save(member);
        return "가입되었습니다.";
    }

    @Transactional
    public JwtToken signIn(String loginId, String password){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginId, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        JwtToken tokenInfo = jwtTokenProvider.generateToken(authentication);

        return tokenInfo;
    }
}
