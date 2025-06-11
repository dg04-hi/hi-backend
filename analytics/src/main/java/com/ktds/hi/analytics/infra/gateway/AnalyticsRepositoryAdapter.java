package com.ktds.hi.analytics.infra.gateway;

import com.ktds.hi.analytics.biz.domain.Analytics;
import com.ktds.hi.analytics.biz.domain.AiFeedback;
import com.ktds.hi.analytics.biz.usecase.out.AnalyticsPort;
import com.ktds.hi.analytics.infra.gateway.entity.AnalyticsEntity;
import com.ktds.hi.analytics.infra.gateway.entity.AiFeedbackEntity;
import com.ktds.hi.analytics.infra.gateway.repository.AnalyticsJpaRepository;
import com.ktds.hi.analytics.infra.gateway.repository.AiFeedbackJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Arrays;
import java.util.List;

/**
 * 분석 리포지토리 어댑터 클래스
 * Analytics Port를 구현하여 데이터 영속성 기능을 제공
 */
@Component
@RequiredArgsConstructor
public class AnalyticsRepositoryAdapter implements AnalyticsPort {
    
    private final AnalyticsJpaRepository analyticsJpaRepository;
    private final AiFeedbackJpaRepository aiFeedbackJpaRepository;
    
    @Override
    public Optional<Analytics> findAnalyticsByStoreId(Long storeId) {
        return analyticsJpaRepository.findByStoreId(storeId)
                .map(this::toDomain);
    }
    
    @Override
    public Analytics saveAnalytics(Analytics analytics) {
        AnalyticsEntity entity = toEntity(analytics);
        AnalyticsEntity saved = analyticsJpaRepository.save(entity);
        return toDomain(saved);
    }
    
    @Override
    public Optional<AiFeedback> findAIFeedbackByStoreId(Long storeId) {
        return aiFeedbackJpaRepository.findByStoreId(storeId)
                .map(this::toAiFeedbackDomain);
    }
    
    @Override
    public AiFeedback saveAIFeedback(AiFeedback feedback) {
        AiFeedbackEntity entity = toAiFeedbackEntity(feedback);
        AiFeedbackEntity saved = aiFeedbackJpaRepository.save(entity);
        return toAiFeedbackDomain(saved);
    }
    
    /**
     * Entity를 Domain으로 변환
     */
    private Analytics toDomain(AnalyticsEntity entity) {
        return Analytics.builder()
                .id(entity.getId())
                .storeId(entity.getStoreId())
                .totalReviews(entity.getTotalReviews())
                .averageRating(entity.getAverageRating())
                .sentimentScore(entity.getSentimentScore())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    /**
     * Domain을 Entity로 변환
     */
    private AnalyticsEntity toEntity(Analytics domain) {
        return AnalyticsEntity.builder()
                .id(domain.getId())
                .storeId(domain.getStoreId())
                .totalReviews(domain.getTotalReviews())
                .averageRating(domain.getAverageRating())
                .sentimentScore(domain.getSentimentScore())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
    
    /**
     * AiFeedback Entity를 Domain으로 변환
     */
    private AiFeedback toAiFeedbackDomain(AiFeedbackEntity entity) {
        return AiFeedback.builder()
                .id(entity.getId())
                .storeId(entity.getStoreId())
                .summary(entity.getSummary())
                .sentiment(entity.getSentiment())
                .positivePoints(entity.getPositivePointsJson() != null ? 
                    parseJsonList(entity.getPositivePointsJson()) : List.of())
                .negativePoints(entity.getNegativePointsJson() != null ? 
                    parseJsonList(entity.getNegativePointsJson()) : List.of())
                .recommendations(entity.getRecommendationsJson() != null ? 
                    parseJsonList(entity.getRecommendationsJson()) : List.of())
                .confidence(entity.getConfidence())
                .analysisDate(entity.getAnalysisDate())
                .createdAt(entity.getCreatedAt())
                .build();
    }
    
    /**
     * AiFeedback Domain을 Entity로 변환
     */
    private AiFeedbackEntity toAiFeedbackEntity(AiFeedback domain) {
        return AiFeedbackEntity.builder()
                .id(domain.getId())
                .storeId(domain.getStoreId())
                .summary(domain.getSummary())
                .sentiment(domain.getSentiment())
                .positivePointsJson(toJsonString(domain.getPositivePoints()))
                .negativePointsJson(toJsonString(domain.getNegativePoints()))
                .recommendationsJson(toJsonString(domain.getRecommendations()))
                .confidence(domain.getConfidence())
                .analysisDate(domain.getAnalysisDate())
                .createdAt(domain.getCreatedAt())
                .build();
    }
    
    /**
     * JSON 문자열을 List로 파싱
     */
    private List<String> parseJsonList(String json) {
        // 실제로는 Jackson 등을 사용하여 파싱
        if (json == null || json.isEmpty()) {
            return List.of();
        }
        return Arrays.asList(json.replace("[", "").replace("]", "").replace("\"", "").split(","));
    }
    
    /**
     * List를 JSON 문자열로 변환
     */
    private String toJsonString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        return "[\"" + String.join("\",\"", list) + "\"]";
    }
}
