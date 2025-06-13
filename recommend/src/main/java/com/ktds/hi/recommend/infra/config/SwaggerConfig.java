package com.ktds.hi.recommend.infra.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
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

    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "Bearer Authentication";

        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .info(new Info()
                        .title("하이오더 취향 추천 서비스 API")
                        .description("사용자 취향 기반 매장 추천, 취향 분석, 추천 알고리즘 등 추천 관련 기능을 제공하는 API")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}