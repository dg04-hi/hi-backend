package com.ktds.hi.common;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Common 모듈 설정 클래스
 * 다른 모듈에서 Common 모듈을 사용할 때 필요한 설정을 자동으로 적용
 */
@Configuration
@ComponentScan(basePackages = "com.ktds.hi.common")
@EntityScan(basePackages = "com.ktds.hi.common.entity")
@EnableJpaRepositories(basePackages = "com.ktds.hi.common.repository")
public class CommonModuleConfiguration {
    // 설정 클래스는 어노테이션만으로도 충분
}
