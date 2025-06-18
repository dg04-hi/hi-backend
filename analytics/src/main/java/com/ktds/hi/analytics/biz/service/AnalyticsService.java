package com.ktds.hi.analytics.biz.service;

import com.ktds.hi.analytics.biz.domain.ActionPlan;
import com.ktds.hi.analytics.biz.domain.Analytics;
import com.ktds.hi.analytics.biz.domain.AiFeedback;
import com.ktds.hi.analytics.biz.domain.PlanStatus;
import com.ktds.hi.analytics.biz.usecase.in.AnalyticsUseCase;
import com.ktds.hi.analytics.biz.usecase.out.*;
import com.ktds.hi.analytics.infra.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 분석 서비스 구현 클래스 (수정버전)
 * Clean Architecture의 UseCase를 구현하여 비즈니스 로직을 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AnalyticsService implements AnalyticsUseCase {
    
    private final AnalyticsPort analyticsPort;
    private final AIServicePort aiServicePort;
    private final ExternalReviewPort externalReviewPort;
    private final OrderDataPort orderDataPort;
    private final CachePort cachePort;
    private final EventPort eventPort;
    private final ActionPlanPort actionPlanPort; // 추가된 의존성
    
    @Override
    // @Cacheable(value = "storeAnalytics", key = "#storeId")
    public StoreAnalyticsResponse getStoreAnalytics(Long storeId) {
        log.info("매장 분석 데이터 조회 시작: storeId={}", storeId);
        
        try {
            // 1. 캐시에서 먼저 확인
            String cacheKey = "analytics:store:" + storeId;
            var cachedResult = cachePort.getAnalyticsCache(cacheKey);
            if (cachedResult.isPresent()) {
                Object cached = cachedResult.get();
                // StoreAnalyticsResponse 타입인지 확인
                if (cached instanceof StoreAnalyticsResponse) {
                    log.info("캐시에서 분석 데이터 반환: storeId={}", storeId);
                    return (StoreAnalyticsResponse) cached;
                }
                // LinkedHashMap인 경우 스킵하고 DB에서 조회
                log.debug("캐시 데이터 타입 불일치, DB에서 조회: storeId={}, type={}",
                    storeId, cached.getClass().getSimpleName());
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
    
    // ... 나머지 메서드들은 이전과 동일 ...
    
    @Override
    // @Cacheable(value = "aiFeedback", key = "#storeId")
    public AiFeedbackDetailResponse getAIFeedbackDetail(Long storeId) {
        log.info("AI 피드백 상세 조회 시작: storeId={}", storeId);
        
        try {
            // 1. 캐시에서 먼저 확인 (타입 안전성 보장)
            String cacheKey = "ai_feedback_detail:store:" + storeId;
            var cachedResult = cachePort.getAnalyticsCache(cacheKey);
            if (cachedResult.isPresent()) {
                Object cached = cachedResult.get();
                if (cached instanceof AiFeedbackDetailResponse) {
                    log.info("캐시에서 AI 피드백 반환: storeId={}", storeId);
                    return (AiFeedbackDetailResponse) cached;
                }
                log.debug("AI 피드백 캐시 데이터 타입 불일치, DB에서 조회: storeId={}", storeId);
            }

            // 1. 기존 AI 피드백 조회
            var aiFeedback = analyticsPort.findAIFeedbackByStoreId(storeId);
            
            if (aiFeedback.isEmpty()) {
                // 2. AI 피드백이 없으면 새로 생성
                aiFeedback = Optional.of(generateAIFeedback(storeId));
            }


            // 3. 응답 생성
            AiFeedbackDetailResponse response = AiFeedbackDetailResponse.builder()
                    .feedbackId(aiFeedback.get().getId())
                    .storeId(storeId)
                    .summary(aiFeedback.get().getSummary())
                    .positivePoints(aiFeedback.get().getPositivePoints())
                    .negativePoints(aiFeedback.get().getNegativePoints())
                    .improvementPoints(aiFeedback.get().getImprovementPoints())
                    .recommendations(aiFeedback.get().getRecommendations())
                    .sentimentAnalysis(aiFeedback.get().getSentimentAnalysis())
                    .confidenceScore(aiFeedback.get().getConfidenceScore())
                    .generatedAt(aiFeedback.get().getGeneratedAt())
                    .build();

            //(추가) 실행계획을 조회해서, 이미 생성된 improvementPoints인지 판단
            List<String> actionPlanTitleList = actionPlanPort.findActionPlanTitleByFeedbackId(aiFeedback.get().getId());
            log.info("실행계획 확인 => {}", actionPlanTitleList.toString());
            response.updateImprovementCheck(actionPlanTitleList); //이미 생성된 실행계획 추가.
            
            log.info("AI 피드백 상세 조회 완료: storeId={}", storeId);
            return response;
            
        } catch (Exception e) {
            log.error("AI 피드백 조회 중 오류 발생: storeId={}", storeId, e);
            throw new RuntimeException("AI 피드백 조회에 실패했습니다.", e);
        }
    }
    
    // 나머지 메서드들과 private 메서드들은 이전과 동일하게 구현
    // ... (getStoreStatistics, getAIFeedbackSummary, getReviewAnalysis 등)
    
    @Override
    public StoreStatisticsResponse getStoreStatistics(Long storeId, LocalDate startDate, LocalDate endDate) {
        log.info("매장 통계 조회 시작: storeId={}, startDate={}, endDate={}", storeId, startDate, endDate);

        try {
            // 1. 캐시 키 생성 및 확인
            String cacheKey = String.format("statistics:store:%d:%s:%s", storeId, startDate, endDate);
            var cachedResult = cachePort.getAnalyticsCache(cacheKey);
            if (cachedResult.isPresent()) {
                Object cached = cachedResult.get();
                if (cached instanceof StoreStatisticsResponse) {
                    log.info("캐시에서 통계 데이터 반환: storeId={}", storeId);
                    return (StoreStatisticsResponse) cached;
                }
            }

            // 2. 주문 통계 데이터 조회 (실제 OrderStatistics 도메인 필드 사용)
            var orderStatistics = orderDataPort.getOrderStatistics(storeId, startDate, endDate);

            // 3. 응답 생성
            StoreStatisticsResponse response = StoreStatisticsResponse.builder()
                .storeId(storeId)
                .startDate(startDate)
                .endDate(endDate)
                .totalOrders(orderStatistics.getTotalOrders())
                .totalRevenue(orderStatistics.getTotalRevenue())
                .averageOrderValue(orderStatistics.getAverageOrderValue())
                .peakHour(orderStatistics.getPeakHour())
                .popularMenus(orderStatistics.getPopularMenus())
                .customerAgeDistribution(orderStatistics.getCustomerAgeDistribution())
                .build();

            // 4. 캐시에 저장
            cachePort.putAnalyticsCache(cacheKey, response, java.time.Duration.ofMinutes(30));

            log.info("매장 통계 조회 완료: storeId={}", storeId);
            return response;

        } catch (Exception e) {
            log.error("매장 통계 조회 중 오류 발생: storeId={}", storeId, e);
            throw new RuntimeException("매장 통계 조회에 실패했습니다.", e);
        }
    }
    
    @Override
    public AiFeedbackSummaryResponse getAIFeedbackSummary(Long storeId) {
        log.info("AI 피드백 요약 조회 시작: storeId={}", storeId);

        try {
            // 1. 캐시에서 확인
            String cacheKey = "ai_feedback_summary:store:" + storeId;
            var cachedResult = cachePort.getAnalyticsCache(cacheKey);
            if (cachedResult.isPresent()) {
                Object cached = cachedResult.get();
                if (cached instanceof AiFeedbackSummaryResponse) {
                    return (AiFeedbackSummaryResponse) cached;
                }
            }

            // 2. AI 피드백 조회
            var aiFeedback = analyticsPort.findAIFeedbackByStoreId(storeId);

            if (aiFeedback.isEmpty()) {
                // 3. 피드백이 없으면 기본 응답 생성
                AiFeedbackSummaryResponse emptyResponse = AiFeedbackSummaryResponse.builder()
                    .storeId(storeId)
                    .hasData(false)
                    .message("분석할 데이터가 부족합니다.")
                    .lastUpdated(LocalDateTime.now())
                    .build();

                cachePort.putAnalyticsCache(cacheKey, emptyResponse, java.time.Duration.ofHours(1));
                return emptyResponse;
            }

            // 4. 응답 생성
            AiFeedbackSummaryResponse response = AiFeedbackSummaryResponse.builder()
                .storeId(storeId)
                .hasData(true)
                .message("AI 분석이 완료되었습니다.")
                .overallScore(aiFeedback.get().getConfidenceScore())
                .keyInsight(aiFeedback.get().getSummary())
                .priorityRecommendation(getFirstRecommendation(aiFeedback.get()))
                .lastUpdated(aiFeedback.get().getUpdatedAt())
                .build();

            // 5. 캐시에 저장
            cachePort.putAnalyticsCache(cacheKey, response, java.time.Duration.ofHours(2));

            log.info("AI 피드백 요약 조회 완료: storeId={}", storeId);
            return response;

        } catch (Exception e) {
            log.error("AI 피드백 요약 조회 중 오류 발생: storeId={}", storeId, e);
            throw new RuntimeException("AI 피드백 요약 조회에 실패했습니다.", e);
        }
    }
    
    @Override
    public ReviewAnalysisResponse getReviewAnalysis(Long storeId, int days) {
        log.info("리뷰 분석 조회 시작: storeId={}", storeId);

        try {
            // 1. 캐시에서 확인
            String cacheKey = "review_analysis:store:" + storeId;
            var cachedResult = cachePort.getAnalyticsCache(cacheKey);
            if (cachedResult.isPresent()) {
                Object cached = cachedResult.get();
                if (cached instanceof ReviewAnalysisResponse) {
                    return (ReviewAnalysisResponse) cached;
                }
            }

            // 2. 최근 리뷰 데이터 조회 (30일)
            List<String> recentReviews = externalReviewPort.getRecentReviews(storeId, days);

            if (recentReviews.isEmpty()) {
                ReviewAnalysisResponse emptyResponse = ReviewAnalysisResponse.builder()
                    .storeId(storeId)
                    .totalReviews(0)
                    .positiveReviewCount(0)
                    .negativeReviewCount(0)
                    .positiveRate(0.0)
                    .negativeRate(0.0)
                    .analysisDate(LocalDate.now())
                    .build();

                cachePort.putAnalyticsCache(cacheKey, emptyResponse, java.time.Duration.ofHours(1));
                return emptyResponse;
            }

            // 3. 응답 생성
            int positiveCount = countPositiveReviews(recentReviews);
            int negativeCount = countNegativeReviews(recentReviews);
            int totalCount = recentReviews.size();

            ReviewAnalysisResponse response = ReviewAnalysisResponse.builder()
                .storeId(storeId)
                .totalReviews(totalCount)
                .positiveReviewCount(positiveCount)
                .negativeReviewCount(negativeCount)
                .positiveRate(Math.floor((double) positiveCount / totalCount * 100) / 10.0)
                .negativeRate(Math.floor((double) negativeCount / totalCount * 100) / 10.0)
                .analysisDate(LocalDate.now())
                .build();

            // 4. 캐시에 저장
            cachePort.putAnalyticsCache(cacheKey, response, java.time.Duration.ofHours(4));

            log.info("리뷰 분석 조회 완료: storeId={}", storeId);
            return response;

        } catch (Exception e) {
            log.error("리뷰 분석 중 오류 발생: storeId={}", storeId, e);
            throw new RuntimeException("리뷰 분석에 실패했습니다.", e);
        }
    }
    
    // private 메서드들
    @Transactional
    public Analytics generateNewAnalytics(Long storeId) {
        log.info("새로운 분석 데이터 생성 시작: storeId={}", storeId);

        try {
            // 1. 리뷰 데이터 수집
            List<String> reviewData = externalReviewPort.getReviewData(storeId);
            int totalReviews = reviewData.size();

            if (totalReviews == 0) {
                log.warn("리뷰 데이터가 없어 기본값으로 분석 데이터 생성: storeId={}", storeId);
                return createDefaultAnalytics(storeId);
            }

            // 2. 기본 통계 계산
            double averageRating = 4.0; // 기본값
            double sentimentScore = 0.5; // 중립
            double positiveRate = 60.0;
            double negativeRate = 20.0;

            // 3. Analytics 도메인 객체 생성
            Analytics analytics = Analytics.builder()
                .storeId(storeId)
                .totalReviews(totalReviews)
                .averageRating(averageRating)
                .sentimentScore(sentimentScore)
                .positiveReviewRate(positiveRate)
                .negativeReviewRate(negativeRate)
                .lastAnalysisDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

            // 4. 데이터베이스에 저장
            Analytics saved = analyticsPort.saveAnalytics(analytics);

            log.info("새로운 분석 데이터 생성 완료: storeId={}", storeId);
            return saved;

        } catch (Exception e) {
            log.error("분석 데이터 생성 중 오류 발생: storeId={}", storeId, e);
            return createDefaultAnalytics(storeId);
        }
    }
    
    @Transactional
    public AiFeedback generateAIFeedback(Long storeId) {
        log.info("AI 피드백 생성 시작: storeId={}", storeId);

        try {
            // 1. 최근 30일 리뷰 데이터 수집
            List<String> reviewData = externalReviewPort.getRecentReviews(storeId, 30);

            if (reviewData.isEmpty()) {
                log.warn("AI 피드백 생성을 위한 리뷰 데이터가 없습니다: storeId={}", storeId);
                return createDefaultAIFeedback(storeId);
            }

            // 2. AI 피드백 생성 (실제로는 AI 서비스 호출)
            AiFeedback aiFeedback = AiFeedback.builder()
                .storeId(storeId)
                .summary("고객들의 전반적인 만족도가 높습니다.")
                .positivePoints(List.of("맛이 좋다", "서비스가 친절하다", "분위기가 좋다"))
                .improvementPoints(List.of("대기시간 단축", "가격 경쟁력", "메뉴 다양성"))
                .recommendations(List.of("특별 메뉴 개발", "예약 시스템 도입", "고객 서비스 교육"))
                .sentimentAnalysis("POSITIVE")
                .confidenceScore(0.85)
                .generatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

            // 3. 데이터베이스에 저장
            AiFeedback saved = analyticsPort.saveAIFeedback(aiFeedback);

            log.info("AI 피드백 생성 완료: storeId={}", storeId);
            return saved;

        } catch (Exception e) {
            log.error("AI 피드백 생성 중 오류 발생: storeId={}", storeId, e);
            return createDefaultAIFeedback(storeId);
        }


    }

    private Analytics createDefaultAnalytics(Long storeId) {
        return Analytics.builder()
            .storeId(storeId)
            .totalReviews(0)
            .averageRating(0.0)
            .sentimentScore(0.0)
            .positiveReviewRate(0.0)
            .negativeReviewRate(0.0)
            .lastAnalysisDate(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    private AiFeedback createDefaultAIFeedback(Long storeId) {
        return AiFeedback.builder()
            .storeId(storeId)
            .summary("분석할 리뷰 데이터가 부족합니다.")
            .positivePoints(List.of("데이터 부족으로 분석 불가"))
            .improvementPoints(List.of("리뷰 데이터 수집 필요"))
            .recommendations(List.of("고객들의 리뷰 작성을 유도해보세요"))
            .sentimentAnalysis("NEUTRAL")
            .confidenceScore(0.0)
            .generatedAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    private String getFirstRecommendation(AiFeedback feedback) {
        if (feedback.getRecommendations() != null && !feedback.getRecommendations().isEmpty()) {
            return feedback.getRecommendations().get(0);
        }
        return "추천사항이 없습니다.";
    }

    private int countPositiveReviews(List<String> reviews) {
        // 실제로는 AI 서비스를 통한 감정 분석 필요
        return (int) (reviews.size() * 0.6); // 60% 가정
    }

    private int countNegativeReviews(List<String> reviews) {
        // 실제로는 AI 서비스를 통한 감정 분석 필요
        return (int) (reviews.size() * 0.2); // 20% 가정
    }

    @Override
    @Transactional
    public AiAnalysisResponse generateAIAnalysis(Long storeId, AiAnalysisRequest request) {
        log.info("AI 분석 시작: storeId={}, days={}", storeId, request.getDays());

        try {
            // 1. 기존 generateAIFeedback 메서드를 실제 AI 호출로 수정하여 사용
            AiFeedback aiFeedback = generateRealAIFeedback(storeId, request.getDays());

            // 2. 실행계획 생성 (요청 시)
            List<String> actionPlans = null;
            //TODO : 추후에 AI 분석후에 바로 실행계획까지 생성해야 한다면 추가.
            // if (Boolean.TRUE.equals(request.getGenerateActionPlan())) {
            //     actionPlans = aiServicePort.generateActionPlan(aiFeedback);
            // }

            // 3. 응답 생성
            AiAnalysisResponse response = AiAnalysisResponse.builder()
                .storeId(storeId)
                .feedbackId(aiFeedback.getId())
                .summary(aiFeedback.getSummary())
                .positivePoints(aiFeedback.getPositivePoints())
                .negativePoints(aiFeedback.getNegativePoints())
                .improvementPoints(aiFeedback.getImprovementPoints())
                .recommendations(aiFeedback.getRecommendations())
                .sentimentAnalysis(aiFeedback.getSentimentAnalysis())
                .confidenceScore(aiFeedback.getConfidenceScore())
                .totalReviewsAnalyzed(getTotalReviewsCount(storeId, request.getDays()))
                .actionPlans(actionPlans) //TODO : 사용하는 값은 아니지만 의존성을 위해 그대로 둠, 추후에 변경 필요.
                .analyzedAt(aiFeedback.getGeneratedAt())
                .build();

            log.info("AI 분석 완료: storeId={}, feedbackId={}", storeId, aiFeedback.getId());
            return response;

        } catch (Exception e) {
            log.error("AI 분석 중 오류 발생: storeId={}", storeId, e);
            return createErrorAnalysisResponse(storeId);
        }
    }

    @Override
    @Transactional
    public List<String> generateActionPlansFromFeedback(ActionPlanCreateRequest request, Long feedbackId) {
        log.info("실행계획 생성: feedbackId={}", feedbackId);

        try {
            // 1. AI 피드백 조회
            var aiFeedback = analyticsPort.findAIFeedbackById(feedbackId);

            if (aiFeedback.isEmpty()) {
                throw new RuntimeException("AI 피드백을 찾을 수 없습니다: " + feedbackId);
            }

            AiFeedback feedback = aiFeedback.get();
            // 2. 기존 AIServicePort.generateActionPlan 메서드 활용
            List<String> actionPlans = aiServicePort.generateActionPlan(request.getActionPlanSelect(), aiFeedback.get());


            // 3. DB에 실행계획 저장
            saveGeneratedActionPlansToDatabase(request.getActionPlanSelect(), feedback, actionPlans);

            log.info("실행계획 생성 완료: feedbackId={}, planCount={}", feedbackId, actionPlans.size());
            return actionPlans;

        } catch (Exception e) {
            log.error("실행계획 생성 중 오류 발생: feedbackId={}", feedbackId, e);
            return List.of("서비스 개선을 위한 기본 실행계획을 수립하세요.");
        }
    }

    /**
     * 실제 AI를 호출하는 개선된 피드백 생성 메서드
     * 기존 generateAIFeedback()의 하드코딩 부분을 실제 AI 호출로 수정
     */
    @Transactional
    public AiFeedback generateRealAIFeedback(Long storeId, Integer days) {
        log.info("실제 AI 피드백 생성: storeId={}, days={}", storeId, days);

        try {
            // 1. 리뷰 데이터 수집
            List<String> reviewData = externalReviewPort.getRecentReviews(storeId, days);

            if (reviewData.isEmpty()) {
                log.warn("AI 피드백 생성을 위한 리뷰 데이터가 없습니다: storeId={}", storeId);
                return createDefaultAIFeedback(storeId);
            }

            // 2. 실제 AI 서비스 호출 (기존 하드코딩 부분을 수정)
            AiFeedback aiFeedback = aiServicePort.generateFeedback(reviewData);



            // 3. 도메인 객체 속성 설정
            AiFeedback completeAiFeedback = AiFeedback.builder()
                .storeId(storeId)
                .summary(aiFeedback.getSummary())
                .positivePoints(aiFeedback.getPositivePoints())
                .negativePoints(aiFeedback.getNegativePoints())
                .improvementPoints(aiFeedback.getImprovementPoints())
                .recommendations(aiFeedback.getRecommendations())
                .sentimentAnalysis(aiFeedback.getSentimentAnalysis())
                .confidenceScore(aiFeedback.getConfidenceScore())
                .generatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

            // 4. 데이터베이스에 저장
            AiFeedback saved = analyticsPort.saveAIFeedback(completeAiFeedback);

            log.info("실제 AI 피드백 생성 완료: storeId={}, reviewCount={}", storeId, reviewData.size());
            return saved;

        } catch (Exception e) {
            log.error("실제 AI 피드백 생성 중 오류 발생: storeId={}", storeId, e);
            return createDefaultAIFeedback(storeId);
        }
    }

    private Integer getTotalReviewsCount(Long storeId, Integer days) {
        try {
            return externalReviewPort.getRecentReviews(storeId, days).size();
        } catch (Exception e) {
            log.warn("리뷰 수 조회 실패: storeId={}", storeId, e);
            return 0;
        }
    }

    private AiAnalysisResponse createErrorAnalysisResponse(Long storeId) {
        return AiAnalysisResponse.builder()
            .storeId(storeId)
            .summary("분석 중 오류가 발생했습니다.")
            .positivePoints(List.of("현재 분석이 불가능합니다"))
            .improvementPoints(List.of("시스템 안정화 후 재시도 필요"))
            .recommendations(List.of("잠시 후 다시 분석을 요청해주세요"))
            .sentimentAnalysis("분석 실패")
            .confidenceScore(0.0)
            .totalReviewsAnalyzed(0)
            .actionPlans(List.of("기본 실행계획을 수립하세요"))
            .analyzedAt(LocalDateTime.now())
            .build();
    }

    /**
     * 생성된 실행계획을 데이터베이스에 저장하는 메서드
     * AI 피드백 기반으로 생성된 실행계획들을 ActionPlan 테이블에 저장
     */
    private void saveGeneratedActionPlansToDatabase(List<String> actionPlanSelect, AiFeedback feedback, List<String> actionPlans) {
        if (actionPlans.isEmpty()) {
            log.info("저장할 실행계획이 없습니다: storeId={}", feedback.getStoreId());
            return;
        }

        log.info("실행계획 DB 저장 시작: storeId={}, feedbackId={}, planCount={}",
            feedback.getStoreId(), feedback.getId(), actionPlans.size());

        for (int i = 0; i < actionPlans.size(); i++) {
            String planContent = actionPlans.get(i);

            // ActionPlan 도메인 객체 생성 (기존 ActionPlanService의 패턴과 동일하게)
            ActionPlan actionPlan = ActionPlan.builder()
                .storeId(feedback.getStoreId())
                .userId(0L) // AI가 생성한 계획이므로 userId는 0
                .feedbackId(feedback.getId())
                .title(actionPlanSelect.get(i))
                .description(planContent)
                .period("1개월") // 기본 실행 기간
                .status(PlanStatus.PLANNED)
                .tasks(List.of(planContent)) // 생성된 계획을 tasks로 설정
                .note("AI 피드백(ID: " + feedback.getId() + ")을 기반으로 자동 생성된 실행계획")
                .createdAt(LocalDateTime.now())
                .build();

            try {
                // ActionPlan 저장 (기존 ActionPlanPort 활용)
                ActionPlan savedPlan = actionPlanPort.saveActionPlan(actionPlan);
                log.info("실행계획 저장 완료: storeId={}, planId={}, title={}",
                    feedback.getStoreId(), savedPlan.getId(), savedPlan.getTitle());

            } catch (Exception e) {
                log.error("실행계획 저장 실패: storeId={}, title={}",
                    feedback.getStoreId(), actionPlan.getTitle(), e);
                // 개별 저장 실패 시에도 다음 계획은 계속 저장 시도
            }
        }

        log.info("실행계획 DB 저장 완료: storeId={}, 총 {}개 계획 저장",
            feedback.getStoreId(), actionPlans.size());
    }
}
