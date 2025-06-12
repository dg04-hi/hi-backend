package com.ktds.hi.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

/**
 * 추천 서비스 메인 애플리케이션 클래스
 * 가게 추천, 취향 분석 기능을 제공
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@SpringBootApplication(scanBasePackages = {
        "com.ktds.hi.store",
        "com.ktds.hi.common"
})
@EnableJpaRepositories(basePackages = {
        "com.ktds.hi.store.infra.gateway.repository",  // 👈 MenuJpaRepository 패키지
        "com.ktds.hi.common.repository"
})
@EntityScan(basePackages = {
        "com.ktds.hi.store.infra.gateway.entity",
        "com.ktds.hi.common.entity"
})
@EnableJpaAuditing(auditorAwareRef = "customAuditorAware")
public class StoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreApplication.class, args);
    }

    // 기존 빈들...
    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)  // 👈 다른 빈이 없을 때만 생성
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}