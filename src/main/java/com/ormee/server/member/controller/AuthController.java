package com.ormee.server.member.controller;

import com.ormee.server.global.config.jwt.JwtTokenProvider;
import com.ormee.server.global.config.jwt.JwtToken;
import com.ormee.server.global.response.ResponseDto;
import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.global.config.jwt.RefreshToken;

import com.ormee.server.member.repository.RefreshTokenRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthController(JwtTokenProvider jwtTokenProvider, RefreshTokenRepository refreshTokenRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @PostMapping("/reissue")
    public ResponseDto reissueToken(@RequestHeader("Authorization") String refreshToken) {
        // "Bearer " 제거
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        // Refresh Token 검증
        try {
            jwtTokenProvider.validateToken(refreshToken);
        } catch (CustomException e) {
            throw new CustomException(ExceptionType.INVALID_JWT_EXCEPTION);
        }

        // 저장된 Refresh Token과 비교
        Optional<RefreshToken> storedToken = refreshTokenRepository.findByRefreshToken(refreshToken);
        if (storedToken.isEmpty() || !storedToken.get().getRefreshToken().equals(refreshToken)) {
            throw new CustomException(ExceptionType.INVALID_JWT_EXCEPTION);
        }

        // 새로운 Access Token 생성
        JwtToken newToken = jwtTokenProvider.reissueAccessToken(refreshToken);
        return ResponseDto.success(newToken);
    }
}
