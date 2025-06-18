package com.ktds.hi.analytics.biz.usecase.out;

import com.ktds.hi.analytics.biz.domain.AiFeedback;
import com.ktds.hi.analytics.biz.domain.SentimentType;

import java.util.List;
import java.util.Map;

/**
 * AI 서비스 포트 인터페이스
 * 외부 AI API 연동을 위한 출력 포트
 */
public interface AIServicePort {
    
    /**
     * AI 피드백 생성
     */
    AiFeedback generateFeedback(List<String> reviewData);
    
    /**
     * 감정 분석
     */
    SentimentType analyzeSentiment(String content);

    /**
     * 대량 리뷰 감정 분석 (새로 추가)
     * 여러 리뷰를 한 번에 분석하여 긍정/부정/중립 개수 반환
     *
     * @param reviews 분석할 리뷰 목록
     * @return 감정 타입별 개수 맵
     */
    Map<SentimentType, Integer> analyzeBulkSentiments(List<String> reviews);
    
    /**
     * 실행 계획 생성
     */
    List<String> generateActionPlan(List<String> actionPlanSelect, AiFeedback feedback);
}
