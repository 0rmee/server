package com.ormee.server.global.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.global.response.ResponseDto;
import com.ormee.server.global.exception.CustomException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    private static final List<String> EXCLUDED_URLS = List.of(
            "/members/**",
            "/teachers/signup",
            "/teachers/signin",
            "/students/signup",
            "/students/signin",
            "/students/email/**",
            "/students/username/**",
            "/auth/reissue"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private boolean isExcluded(String requestUri) {
        return EXCLUDED_URLS.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestUri));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestUri = httpRequest.getRequestURI();

        try {
            // JWT 인증 제외 경로는 필터 통과
            if (isExcluded(requestUri)) {
                chain.doFilter(request, response);
                return;
            }

            // 1. Request Header에서 JWT 토큰 추출
            String token = resolveToken(httpRequest);

            if (!StringUtils.hasText(token)) {
                throw new CustomException(ExceptionType.INVALID_JWT_EXCEPTION);
            }

            // 2. 토큰 유효성 검사
            if (jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                throw new CustomException(ExceptionType.INVALID_JWT_EXCEPTION);
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

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ") && bearerToken.length() >= 8) {
            return bearerToken.substring(7);
        }
        return null;
    }
}