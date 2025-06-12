package com.ktds.hi.recommend.infra.gateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * 외부 서비스 클라이언트
 * 다른 마이크로서비스와의 HTTP 통신을 담당
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalServiceClient {

    @Qualifier("storeWebClient")
    private final WebClient storeWebClient;

    @Qualifier("reviewWebClient")
    private final WebClient reviewWebClient;

    @Qualifier("memberWebClient")
    private final WebClient memberWebClient;

    /**
     * 매장 정보 조회
     */
    public Mono<Map<String, Object>> getStoreInfo(Long storeId) {
        return storeWebClient
                .get()
                .uri("/api/stores/{storeId}", storeId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .timeout(Duration.ofSeconds(5))
                .doOnError(error -> log.error("매장 정보 조회 실패: storeId={}, error={}", storeId, error.getMessage()))
                .onErrorReturn(Map.of());
    }

    /**
     * 매장 목록 조회 (위치 기반)
     */
    public Mono<List<Map<String, Object>>> getStoresByLocation(Double latitude, Double longitude, Integer radius) {
        return storeWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/stores/search")
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam("radius", radius)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .timeout(Duration.ofSeconds(10))
                .doOnError(error -> log.error("위치 기반 매장 조회 실패: lat={}, lng={}, radius={}, error={}",
                        latitude, longitude, radius, error.getMessage()))
                .onErrorReturn(List.of());
    }

    /**
     * 매장별 리뷰 조회
     */
    public Mono<List<Map<String, Object>>> getStoreReviews(Long storeId, Integer limit) {
        return reviewWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/reviews/store/{storeId}")
                        .queryParam("limit", limit)
                        .build(storeId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .timeout(Duration.ofSeconds(5))
                .doOnError(error -> log.error("매장 리뷰 조회 실패: storeId={}, error={}", storeId, error.getMessage()))
                .onErrorReturn(List.of());
    }

    /**
     * 회원 취향 정보 조회
     */
    public Mono<Map<String, Object>> getMemberPreferences(Long memberId) {
        return memberWebClient
                .get()
                .uri("/api/members/{memberId}/preferences", memberId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .timeout(Duration.ofSeconds(5))
                .doOnError(error -> log.error("회원 취향 정보 조회 실패: memberId={}, error={}", memberId, error.getMessage()))
                .onErrorReturn(Map.of());
    }

    /**
     * 회원 주문 이력 조회
     */
    public Mono<List<Map<String, Object>>> getMemberOrderHistory(Long memberId, Integer limit) {
        return memberWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/members/{memberId}/orders")
                        .queryParam("limit", limit)
                        .build(memberId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .timeout(Duration.ofSeconds(5))
                .doOnError(error -> log.error("회원 주문 이력 조회 실패: memberId={}, error={}", memberId, error.getMessage()))
                .onErrorReturn(List.of());
    }

    /**
     * 매장 평점 및 통계 조회
     */
    public Mono<Map<String, Object>> getStoreStatistics(Long storeId) {
        return storeWebClient
                .get()
                .uri("/api/stores/{storeId}/statistics", storeId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .timeout(Duration.ofSeconds(5))
                .doOnError(error -> log.error("매장 통계 조회 실패: storeId={}, error={}", storeId, error.getMessage()))
                .onErrorReturn(Map.of("rating", 0.0, "reviewCount", 0));
    }

    /**
     * 인기 매장 조회
     */
    public Mono<List<Map<String, Object>>> getPopularStores(String category, Integer limit) {
        return storeWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/stores/popular")
                        .queryParam("category", category)
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .timeout(Duration.ofSeconds(10))
                .doOnError(error -> log.error("인기 매장 조회 실패: category={}, limit={}, error={}",
                        category, limit, error.getMessage()))
                .onErrorReturn(List.of());
    }
}