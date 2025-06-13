package com.ktds.hi.review.infra.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger 설정 클래스
 * API 문서화를 위한 OpenAPI 설정
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .info(new Info()
                        .title("하이오더 리뷰 관리 서비스 API")
                        .description("리뷰 작성, 조회, 삭제, 반응, 댓글 등 리뷰 관련 기능을 제공하는 API")
                        .version("1.0.0"))
                .components(new Components()                                    // 이 줄 추가!
                    .addSecuritySchemes(SECURITY_SCHEME_NAME, createAPIKeyScheme())) // 이 줄 추가!
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME)); // 이 줄 추가!
    }

    /**
     * JWT Bearer 토큰을 위한 Security Scheme 생성
     */
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("""
                        JWT 토큰을 입력하세요
                        
                        사용법:
                        1. 로그인 API로 토큰 발급
                        2. Bearer 접두사 없이 토큰만 입력
                        3. 예: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOi...
                        """);
    }
}

