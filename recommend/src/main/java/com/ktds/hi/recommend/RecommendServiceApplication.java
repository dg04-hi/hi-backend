package com.ktds.hi.recommend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 추천 서비스 메인 애플리케이션
 */
@SpringBootApplication(scanBasePackages = {
        "com.ktds.hi.recommend",
        "com.ktds.hi.common"
})
@EnableFeignClients
@EnableJpaAuditing
public class RecommendServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecommendServiceApplication.class, args);
    }
}