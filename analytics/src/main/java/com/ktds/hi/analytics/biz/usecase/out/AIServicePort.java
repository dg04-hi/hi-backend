package com.ktds.hi.analytics.biz.usecase.out;

import com.ktds.hi.analytics.biz.domain.AiFeedback;
import com.ktds.hi.analytics.biz.domain.SentimentType;

import java.util.List;

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
     * 실행 계획 생성
     */
    List<String> generateActionPlan(AiFeedback feedback);
}
