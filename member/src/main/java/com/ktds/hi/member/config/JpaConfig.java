package com.ktds.hi.member.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA 설정 클래스
 */
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.ktds.hi.member.repository")
public class JpaConfig {
}
