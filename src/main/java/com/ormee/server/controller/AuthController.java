package com.ormee.server.controller;

import com.ormee.server.config.jwt.JwtTokenProvider;
import com.ormee.server.config.jwt.JwtToken;
import com.ormee.server.repository.RefreshTokenRepository;
import com.ormee.server.config.jwt.RefreshToken;

import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> reissueToken(@RequestHeader("Authorization") String refreshToken) {
        // "Bearer " 제거
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        // Refresh Token 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body("유효하지 않은 Refresh Token입니다.");
        }

        // 저장된 Refresh Token과 비교
        Optional<RefreshToken> storedToken = refreshTokenRepository.findByRefreshToken(refreshToken);
        if (storedToken.isEmpty() || !storedToken.get().getRefreshToken().equals(refreshToken)) {
            return ResponseEntity.status(403).body("Refresh Token이 일치하지 않습니다.");
        }

        // 새로운 Access Token 생성
        JwtToken newToken = jwtTokenProvider.reissueAccessToken(refreshToken);
        return ResponseEntity.ok(newToken);
    }
}
