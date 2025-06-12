package com.ktds.hi.review.infra.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 리뷰 서비스 설정 클래스
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.ktds.hi.review.infra.gateway.repository")
@EntityScan(basePackages = "com.ktds.hi.review.infra.gateway.entity")
public class ReviewConfig {
}