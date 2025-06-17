package com.ktds.hi.analytics.infra.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktds.hi.analytics.biz.domain.Analytics;
import com.ktds.hi.analytics.biz.domain.AiFeedback;
import com.ktds.hi.analytics.biz.usecase.out.AnalyticsPort;
import com.ktds.hi.analytics.infra.gateway.entity.AnalyticsEntity;
import com.ktds.hi.analytics.infra.gateway.entity.AiFeedbackEntity;
import com.ktds.hi.analytics.infra.gateway.repository.AnalyticsJpaRepository;
import com.ktds.hi.analytics.infra.gateway.repository.AiFeedbackJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 분석 리포지토리 어댑터 클래스 (완성버전)
 * Analytics Port를 구현하여 데이터 영속성 기능을 제공
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsRepositoryAdapter implements AnalyticsPort {
    
    private final AnalyticsJpaRepository analyticsJpaRepository;
    private final AiFeedbackJpaRepository aiFeedbackJpaRepository;
    private final ObjectMapper objectMapper;
    
    @Override
    public Optional<Analytics> findAnalyticsByStoreId(Long storeId) {
        return analyticsJpaRepository.findLatestByStoreId(storeId)
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
        return aiFeedbackJpaRepository.findLatestByStoreId(storeId)
                .map(this::toAiFeedbackDomain);
    }
    
    @Override
    public AiFeedback saveAIFeedback(AiFeedback feedback) {
        AiFeedbackEntity entity = toAiFeedbackEntity(feedback);
        AiFeedbackEntity saved = aiFeedbackJpaRepository.save(entity);
        return toAiFeedbackDomain(saved);
    }

    @Override
    public Optional<AiFeedback> findAIFeedbackById(Long feedbackId) {
        return aiFeedbackJpaRepository.findById(feedbackId)
            .map(this::toAiFeedbackDomain);
    }
    
    /**
     * Analytics Entity를 Domain으로 변환
     */
    private Analytics toDomain(AnalyticsEntity entity) {
        return Analytics.builder()
                .id(entity.getId())
                .storeId(entity.getStoreId())
                .totalReviews(entity.getTotalReviews())
                .averageRating(entity.getAverageRating())
                .sentimentScore(entity.getSentimentScore())
                .positiveReviewRate(entity.getPositiveReviewRate())
                .negativeReviewRate(entity.getNegativeReviewRate())
                .lastAnalysisDate(entity.getLastAnalysisDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    /**
     * Analytics Domain을 Entity로 변환
     */
    private AnalyticsEntity toEntity(Analytics domain) {
        return AnalyticsEntity.builder()
                .id(domain.getId())
                .storeId(domain.getStoreId())
                .totalReviews(domain.getTotalReviews())
                .averageRating(domain.getAverageRating())
                .sentimentScore(domain.getSentimentScore())
                .positiveReviewRate(domain.getPositiveReviewRate())
                .negativeReviewRate(domain.getNegativeReviewRate())
                .lastAnalysisDate(domain.getLastAnalysisDate())
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
                .positivePoints(parseJsonToList(entity.getPositivePointsJson()))
                .negativePoints(parseJsonToList(entity.getNegativePointsJson()))
                .improvementPoints(parseJsonToList(entity.getImprovementPointsJson()))
                .recommendations(parseJsonToList(entity.getRecommendationsJson()))
                .sentimentAnalysis(entity.getSentimentAnalysis())
                .confidenceScore(entity.getConfidenceScore())
                .generatedAt(entity.getGeneratedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
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
                .positivePointsJson(parseListToJson(domain.getPositivePoints()))
                .negativePointsJson(parseListToJson(domain.getNegativePoints()))
                .improvementPointsJson(parseListToJson(domain.getImprovementPoints()))
                .recommendationsJson(parseListToJson(domain.getRecommendations()))
                .sentimentAnalysis(domain.getSentimentAnalysis())
                .confidenceScore(domain.getConfidenceScore())
                .generatedAt(domain.getGeneratedAt())
                .build();
    }
    
    /**
     * JSON 문자열을 List로 변환
     */
    private List<String> parseJsonToList(String json) {
        if (json == null || json.trim().isEmpty()) {
            return List.of();
        }
        
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            log.warn("JSON 파싱 실패: {}", json, e);
            return List.of();
        }
    }
    
    /**
     * List를 JSON 문자열로 변환
     */
    private String parseListToJson(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            log.warn("JSON 직렬화 실패: {}", list, e);
            return "[]";
        }
    }
}
