package com.ktds.hi.recommend.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 추천 서비스 설정 클래스
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.ktds.hi.recommend.infra.gateway.repository")
public class RecommendConfig {
}
