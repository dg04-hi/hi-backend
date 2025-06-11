package com.ktds.hi.review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 리뷰 관리 서비스 메인 애플리케이션 클래스
 * 리뷰 작성, 조회, 삭제, 반응, 댓글 기능을 제공
 * 
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@SpringBootApplication(scanBasePackages = {"com.ktds.hi.review", "com.ktds.hi.common"})
@EnableJpaAuditing
public class ReviewServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ReviewServiceApplication.class, args);
    }
}
