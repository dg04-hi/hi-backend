package com.ktds.hi.recommend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 추천 서비스 메인 애플리케이션 클래스
 * 가게 추천, 취향 분석 기능을 제공
 * 
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@SpringBootApplication(scanBasePackages = {"com.ktds.hi.recommend", "com.ktds.hi.common"})
@EnableJpaAuditing
public class RecommendServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(RecommendServiceApplication.class, args);
    }
}
