package com.ktds.hi.analytics.biz.usecase.in;

import com.ktds.hi.analytics.infra.dto.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 분석 서비스 UseCase 인터페이스
 * Clean Architecture의 입력 포트 정의
 */
public interface AnalyticsUseCase {
    
    /**
     * 매장 분석 데이터 조회
     */
    StoreAnalyticsResponse getStoreAnalytics(Long storeId);
    
    /**
     * AI 피드백 상세 조회
     */
    AiFeedbackDetailResponse getAIFeedbackDetail(Long storeId);
    
    /**
     * 매장 통계 조회
     */
    StoreStatisticsResponse getStoreStatistics(Long storeId, LocalDate startDate, LocalDate endDate);
    
    /**
     * AI 피드백 요약 조회
     */
    AiFeedbackSummaryResponse getAIFeedbackSummary(Long storeId);
    
    /**
     * 리뷰 분석 조회
     */
    ReviewAnalysisResponse getReviewAnalysis(Long storeId);

    /**
     * AI 리뷰 분석 및 실행계획 생성
     */
    AiAnalysisResponse generateAIAnalysis(Long storeId, AiAnalysisRequest request);

    /**
     * AI 피드백 기반 실행계획 생성
     */
    List<String> generateActionPlansFromFeedback(ActionPlanCreateRequest request,Long feedbackId);

}
