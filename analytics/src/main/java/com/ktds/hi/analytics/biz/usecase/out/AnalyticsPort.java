package com.ktds.hi.analytics.biz.usecase.out;

import com.ktds.hi.analytics.biz.domain.AiFeedback;
import com.ktds.hi.analytics.biz.domain.Analytics;

import java.util.Optional;

/**
 * 분석 데이터 포트 인터페이스
 * Clean Architecture의 출력 포트 정의
 */
public interface AnalyticsPort {
    
    /**
     * 매장 ID로 분석 데이터 조회
     */
    Optional<Analytics> findAnalyticsByStoreId(Long storeId);
    
    /**
     * 분석 데이터 저장
     */
    Analytics saveAnalytics(Analytics analytics);
    
    /**
     * 매장 ID로 AI 피드백 조회
     */
    Optional<AiFeedback> findAIFeedbackByStoreId(Long storeId);

    /**
     * AI 피드백 ID로 조회 (추가된 메서드)
     */
    Optional<AiFeedback> findAIFeedbackById(Long feedbackId);


    /**
     * AI 피드백 저장
     */
    AiFeedback saveAIFeedback(AiFeedback feedback);
}
