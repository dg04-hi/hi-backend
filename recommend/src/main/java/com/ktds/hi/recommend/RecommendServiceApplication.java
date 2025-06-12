package com.ktds.hi.recommend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 추천 서비스 메인 애플리케이션 클래스
 * 가게 추천, 취향 분석 기능을 제공
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@SpringBootApplication(scanBasePackages = {
        "com.ktds.hi.recommend",
        "com.ktds.hi.common"
})
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.ktds.hi.recommend.infra.gateway.repository")
@EntityScan(basePackages = "com.ktds.hi.recommend.infra.gateway.entity")
public class RecommendServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecommendServiceApplication.class, args);
    }
}