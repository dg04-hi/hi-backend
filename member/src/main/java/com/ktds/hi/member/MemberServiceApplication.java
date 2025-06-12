package com.ktds.hi.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 회원 관리 서비스 메인 애플리케이션 클래스
 * 인증, 회원정보 관리, 취향 관리 기능을 제공
 * 
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@SpringBootApplication(scanBasePackages = {"com.ktds.hi.member", "com.ktds.hi.common"})
@EntityScan(basePackages = "com.ktds.hi.member.repository.entity")
public class MemberServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MemberServiceApplication.class, args);
    }
}
