package com.ktds.hi.analytics.biz.service;

import com.ktds.hi.analytics.biz.domain.Analytics;
import com.ktds.hi.analytics.biz.domain.AiFeedback;
import com.ktds.hi.analytics.biz.usecase.in.AnalyticsUseCase;
import com.ktds.hi.analytics.biz.usecase.out.*;
import com.ktds.hi.analytics.infra.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 분석 서비스 구현 클래스
 * Clean Architecture의 UseCase를 구현하여 비즈니스 로직을 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService implements AnalyticsUseCase {
    
    private final AnalyticsPort analyticsPort;
    private final AIServicePort aiServicePort;
    private final ExternalReviewPort externalReviewPort;
    private final OrderDataPort orderDataPort;
    private final CachePort cachePort;
    private final EventPort eventPort;
    
    @Override
    @Cacheable(value = "storeAnalytics", key = "#storeId")
    public StoreAnalyticsResponse getStoreAnalytics(Long storeId) {
        log.info("매장 분석 데이터 조회 시작: storeId={}", storeId);
        
        try {
            // 1. 캐시에서 먼저 확인
            String cacheKey = "analytics:store:" + storeId;
            var cachedResult = cachePort.getAnalyticsCache(cacheKey);
            if (cachedResult.isPresent()) {
                log.info("캐시에서 분석 데이터 반환: storeId={}", storeId);
                return (StoreAnalyticsResponse) cachedResult.get();
            }
            
            // 2. 데이터베이스에서 기존 분석 데이터 조회
            var analytics = analyticsPort.findAnalyticsByStoreId(storeId);
            
            if (analytics.isEmpty()) {
                // 3. 분석 데이터가 없으면 새로 생성
                analytics = Optional.of(generateNewAnalytics(storeId));
            }
            
            // 4. 응답 생성
            StoreAnalyticsResponse response = StoreAnalyticsResponse.builder()
                    .storeId(storeId)
                    .totalReviews(analytics.get().getTotalReviews())
                    .averageRating(analytics.get().getAverageRating())
                    .sentimentScore(analytics.get().getSentimentScore())
                    .positiveReviewRate(analytics.get().getPositiveReviewRate())
                    .negativeReviewRate(analytics.get().getNegativeReviewRate())
                    .lastAnalysisDate(analytics.get().getLastAnalysisDate())
                    .build();
            
            // 5. 캐시에 저장
            cachePort.putAnalyticsCache(cacheKey, response, java.time.Duration.ofHours(1));
            
            log.info("매장 분석 데이터 조회 완료: storeId={}", storeId);
            return response;
            
        } catch (Exception e) {
            log.error("매장 분석 데이터 조회 중 오류 발생: storeId={}", storeId, e);
            throw new RuntimeException("분석 데이터 조회에 실패했습니다.", e);
        }
    }
    
    @Override
    @Cacheable(value = "aiFeedback", key = "#storeId")
    public AiFeedbackDetailResponse getAIFeedbackDetail(Long storeId) {
        log.info("AI 피드백 상세 조회 시작: storeId={}", storeId);
        
        try {
            // 1. 기존 AI 피드백 조회
            var aiFeedback = analyticsPort.findAIFeedbackByStoreId(storeId);
            
            if (aiFeedback.isEmpty()) {
                // 2. AI 피드백이 없으면 새로 생성
                aiFeedback = Optional.of(generateAIFeedback(storeId));
            }
            
            // 3. 응답 생성
            AiFeedbackDetailResponse response = AiFeedbackDetailResponse.builder()
                    .storeId(storeId)
                    .summary(aiFeedback.get().getSummary())
                    .positivePoints(aiFeedback.get().getPositivePoints())
                    .improvementPoints(aiFeedback.get().getImprovementPoints())
                    .recommendations(aiFeedback.get().getRecommendations())
                    .sentimentAnalysis(aiFeedback.get().getSentimentAnalysis())
                    .confidenceScore(aiFeedback.get().getConfidenceScore())
                    .generatedAt(aiFeedback.get().getGeneratedAt())
                    .build();
            
            log.info("AI 피드백 상세 조회 완료: storeId={}", storeId);
            return response;
            
        } catch (Exception e) {
            log.error("AI 피드백 조회 중 오류 발생: storeId={}", storeId, e);
            throw new RuntimeException("AI 피드백 조회에 실패했습니다.", e);
        }
    }
    
    @Override
    public StoreStatisticsResponse getStoreStatistics(Long storeId, LocalDate startDate, LocalDate endDate) {
        log.info("매장 통계 조회 시작: storeId={}, period={} ~ {}", storeId, startDate, endDate);
        
        try {
            // 1. 주문 통계 데이터 조회
            var orderStats = orderDataPort.getOrderStatistics(storeId, startDate, endDate);
            
            // 2. 리뷰 데이터 조회
            var reviewCount = externalReviewPort.getReviewCount(storeId);
            
            // 3. 통계 응답 생성
            StoreStatisticsResponse response = StoreStatisticsResponse.builder()
                    .storeId(storeId)
                    .startDate(startDate)
                    .endDate(endDate)
                    .totalOrders(orderStats.getTotalOrders())
                    .totalRevenue(orderStats.getTotalRevenue())
                    .averageOrderValue(orderStats.getAverageOrderValue())
                    .peakHour(orderStats.getPeakHour())
                    .popularMenus(orderStats.getPopularMenus())
                    .customerAgeDistribution(orderStats.getCustomerAgeDistribution())
                    .totalReviews(reviewCount)
                    .generatedAt(java.time.LocalDateTime.now())
                    .build();
            
            log.info("매장 통계 조회 완료: storeId={}", storeId);
            return response;
            
        } catch (Exception e) {
            log.error("매장 통계 조회 중 오류 발생: storeId={}", storeId, e);
            throw new RuntimeException("통계 조회에 실패했습니다.", e);
        }
    }
    
    @Override
    public AiFeedbackSummaryResponse getAIFeedbackSummary(Long storeId) {
        log.info("AI 피드백 요약 조회 시작: storeId={}", storeId);
        
        try {
            var aiFeedback = analyticsPort.findAIFeedbackByStoreId(storeId);
            
            if (aiFeedback.isEmpty()) {
                return AiFeedbackSummaryResponse.builder()
                        .storeId(storeId)
                        .hasData(false)
                        .message("AI 분석 데이터가 없습니다. 리뷰 데이터를 수집한 후 다시 시도해주세요.")
                        .build();
            }
            
            return AiFeedbackSummaryResponse.builder()
                    .storeId(storeId)
                    .hasData(true)
                    .overallScore(aiFeedback.get().getConfidenceScore())
                    .keyInsight(aiFeedback.get().getSummary())
                    .priorityRecommendation(aiFeedback.get().getRecommendations().get(0))
                    .lastUpdated(aiFeedback.get().getGeneratedAt())
                    .build();
            
        } catch (Exception e) {
            log.error("AI 피드백 요약 조회 중 오류 발생: storeId={}", storeId, e);
            throw new RuntimeException("AI 피드백 요약 조회에 실패했습니다.", e);
        }
    }
    
    @Override
    public ReviewAnalysisResponse getReviewAnalysis(Long storeId) {
        log.info("리뷰 분석 조회 시작: storeId={}", storeId);
        
        try {
            // 1. 최근 리뷰 데이터 조회
            var recentReviews = externalReviewPort.getRecentReviews(storeId, 30);
            
            // 2. 감정 분석 수행
            var sentimentResults = recentReviews.stream()
                    .map(review -> aiServicePort.analyzeSentiment(review))
                    .toList();
            
            // 3. 분석 결과 집계
            long positiveCount = sentimentResults.stream()
                    .mapToLong(sentiment -> sentiment.name().equals("POSITIVE") ? 1 : 0)
                    .sum();
            
            long negativeCount = sentimentResults.stream()
                    .mapToLong(sentiment -> sentiment.name().equals("NEGATIVE") ? 1 : 0)
                    .sum();
            
            double positiveRate = recentReviews.isEmpty() ? 0.0 : 
                    (double) positiveCount / recentReviews.size() * 100;
            
            double negativeRate = recentReviews.isEmpty() ? 0.0 : 
                    (double) negativeCount / recentReviews.size() * 100;
            
            // 4. 응답 생성
            ReviewAnalysisResponse response = ReviewAnalysisResponse.builder()
                    .storeId(storeId)
                    .totalReviews(recentReviews.size())
                    .positiveReviewCount((int) positiveCount)
                    .negativeReviewCount((int) negativeCount)
                    .positiveRate(positiveRate)
                    .negativeRate(negativeRate)
                    .analysisDate(LocalDate.now())
                    .build();
            
            log.info("리뷰 분석 조회 완료: storeId={}", storeId);
            return response;
            
        } catch (Exception e) {
            log.error("리뷰 분석 조회 중 오류 발생: storeId={}", storeId, e);
            throw new RuntimeException("리뷰 분석에 실패했습니다.", e);
        }
    }
    
    /**
     * 새로운 분석 데이터 생성
     */
    @Transactional
    private Analytics generateNewAnalytics(Long storeId) {
        log.info("새로운 분석 데이터 생성 시작: storeId={}", storeId);
        
        try {
            // 1. 리뷰 데이터 수집
            var reviewData = externalReviewPort.getReviewData(storeId);
            
            // 2. AI 분석 수행
            var aiFeedback = aiServicePort.generateFeedback(reviewData);
            
            // 3. 분석 데이터 생성
            Analytics analytics = Analytics.builder()
                    .storeId(storeId)
                    .totalReviews(reviewData.size())
                    .averageRating(calculateAverageRating(reviewData))
                    .sentimentScore(aiFeedback.getConfidenceScore())
                    .positiveReviewRate(calculatePositiveRate(reviewData))
                    .negativeReviewRate(calculateNegativeRate(reviewData))
                    .lastAnalysisDate(java.time.LocalDateTime.now())
                    .build();
            
            // 4. 저장
            Analytics savedAnalytics = analyticsPort.saveAnalytics(analytics);
            analyticsPort.saveAIFeedback(aiFeedback);
            
            // 5. 분석 완료 이벤트 발행
            eventPort.publishAnalysisCompletedEvent(storeId, 
                    com.ktds.hi.analytics.biz.domain.AnalysisType.FULL_ANALYSIS);
            
            log.info("새로운 분석 데이터 생성 완료: storeId={}", storeId);
            return savedAnalytics;
            
        } catch (Exception e) {
            log.error("분석 데이터 생성 중 오류 발생: storeId={}", storeId, e);
            throw new RuntimeException("분석 데이터 생성에 실패했습니다.", e);
        }
    }
    
    /**
     * AI 피드백 생성
     */
    @Transactional
    private AiFeedback generateAIFeedback(Long storeId) {
        log.info("AI 피드백 생성 시작: storeId={}", storeId);
        
        try {
            var reviewData = externalReviewPort.getReviewData(storeId);
            var aiFeedback = aiServicePort.generateFeedback(reviewData);
            
            return analyticsPort.saveAIFeedback(aiFeedback);
            
        } catch (Exception e) {
            log.error("AI 피드백 생성 중 오류 발생: storeId={}", storeId, e);
            throw new RuntimeException("AI 피드백 생성에 실패했습니다.", e);
        }
    }
    
    // 유틸리티 메서드들
    private double calculateAverageRating(List<String> reviewData) {
        // 리뷰 데이터에서 평점 추출 및 평균 계산 로직
        return 4.2; // 임시 값
    }
    
    private double calculatePositiveRate(List<String> reviewData) {
        // 긍정 리뷰 비율 계산 로직
        return 75.5; // 임시 값
    }
    
    private double calculateNegativeRate(List<String> reviewData) {
        // 부정 리뷰 비율 계산 로직
        return 15.2; // 임시 값
    }
}
