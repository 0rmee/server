package com.ormee.server.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.exception.CustomException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            // 1. Request Header에서 JWT 토큰 추출
            String token = resolveToken(httpRequest);

            // 2. validateToken으로 토큰 유효성 검사
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 토큰이 유효할 경우 Authentication 객체를 가져와 SecurityContext에 저장
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            chain.doFilter(request, response);

        } catch (CustomException e) {
            httpResponse.setStatus(e.getExceptionType().getHttpStatus().value());
            httpResponse.setContentType("application/json;charset=UTF-8");

            ResponseDto<?> errorResponse = ResponseDto.fail(
                    e.getExceptionType().getHttpStatus().value(),
                    e.getExceptionType().getMessage()
            );

            String json = new ObjectMapper().writeValueAsString(errorResponse);
            httpResponse.getWriter().write(json);
            httpResponse.getWriter().flush();
        }
    }

    // Request Header에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
