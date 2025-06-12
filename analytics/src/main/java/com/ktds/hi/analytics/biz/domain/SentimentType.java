package com.ktds.hi.analytics.biz.domain;

/**
 * 감정 분석 결과 열거형
 */
public enum SentimentType {
    POSITIVE("긍정"),
    NEGATIVE("부정"),
    NEUTRAL("중립");
    
    private final String description;
    
    SentimentType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
