package com.ktds.hi;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Analytics 서비스 메인 애플리케이션 클래스
 */
@SpringBootApplication(scanBasePackages = {"com.ktds.hi.analytics", "com.ktds.hi.common"})
@EntityScan(basePackages = "com.ktds.hi.analytics.infra.gateway.entity")
@EnableJpaRepositories(basePackages = {
    "com.ktds.hi.analytics.infra.gateway.repository"
})
public class AnalyticsApplication {
    
    public static void main(String[] args) {


        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        SpringApplication.run(AnalyticsApplication.class, args);
    }
}
