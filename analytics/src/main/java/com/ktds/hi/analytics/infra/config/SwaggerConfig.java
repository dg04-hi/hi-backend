package com.ktds.hi.analytics.infra.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger 설정 클래스
 * API 문서화를 위한 OpenAPI 설정
 */
@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Analytics Service API")
                .description("하이오더 분석 서비스 API 문서")
                .version("1.0.0"));
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
