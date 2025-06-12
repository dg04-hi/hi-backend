package com.ktds.hi.recommend.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * 추천 서비스 WebClient 설정 클래스
 * 외부 서비스와의 HTTP 통신을 위한 설정
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Configuration
public class RecommendWebClientConfig {

    @Value("${services.store.url:http://store-service:8082}")
    private String storeServiceUrl;

    @Value("${services.review.url:http://review-service:8083}")
    private String reviewServiceUrl;

    @Value("${services.member.url:http://member-service:8081}")
    private String memberServiceUrl;

    /**
     * 매장 서비스와 통신하기 위한 WebClient
     */
    @Bean("storeWebClient")
    public WebClient storeWebClient() {
        return WebClient.builder()
                .baseUrl(storeServiceUrl)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }

    /**
     * 리뷰 서비스와 통신하기 위한 WebClient
     */
    @Bean("reviewWebClient")
    public WebClient reviewWebClient() {
        return WebClient.builder()
                .baseUrl(reviewServiceUrl)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }

    /**
     * 회원 서비스와 통신하기 위한 WebClient
     */
    @Bean("memberWebClient")
    public WebClient memberWebClient() {
        return WebClient.builder()
                .baseUrl(memberServiceUrl)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }

    /**
     * 일반적인 외부 API 호출을 위한 WebClient
     */
    @Bean("externalWebClient")
    public WebClient externalWebClient() {
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
    }

    /**
     * 호환성을 위한 RestTemplate (레거시 지원)
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}