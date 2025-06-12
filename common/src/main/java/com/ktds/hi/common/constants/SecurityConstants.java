package com.ktds.hi.common.constants;

/**
 * 보안 관련 상수
 */
public class SecurityConstants {

    // JWT 헤더 관련
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    // 토큰 타입
    public static final String TOKEN_TYPE_ACCESS = "access";
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    // 토큰 만료 시간
    public static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L; // 30분
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L; // 7일

    // 공개 엔드포인트 (JWT 인증 제외)
    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/**",
            "/api/members/register",
            "/api/members/check-username/**",
            "/api/members/check-nickname/**",
            "/api/stores/search/**",
            "/api/stores/*/reviews/**",
            "/swagger-ui/**",
            "/api-docs/**",
            "/actuator/**",
            "/error",
            "/favicon.ico"
    };

    private SecurityConstants() {
        // 유틸리티 클래스
    }
}