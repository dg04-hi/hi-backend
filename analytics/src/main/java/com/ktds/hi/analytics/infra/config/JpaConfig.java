package com.ktds.hi.analytics.infra.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA 설정 클래스
 * JPA Auditing 및 Repository 스캔 설정
 */
@Configuration
@EnableJpaRepositories(basePackages = {
 //       "com.ktds.hi.store.infra.gateway.entity",
        "com.ktds.hi.common.repository"
})
@EntityScan(basePackages = {
        "com.ktds.hi.store.infra.gateway.entity",
        "com.ktds.hi.common.entity",
        "com.ktds.hi.common.audit"
})
@EnableJpaAuditing(auditorAwareRef = "customAuditorAware")
public class JpaConfig {
}
