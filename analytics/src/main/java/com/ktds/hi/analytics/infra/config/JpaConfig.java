package com.ktds.hi.analytics.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA 설정 클래스
 * JPA Auditing 및 Repository 스캔 설정
 */
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.ktds.hi.analytics.infra.gateway.repository")
public class JpaConfig {
}
