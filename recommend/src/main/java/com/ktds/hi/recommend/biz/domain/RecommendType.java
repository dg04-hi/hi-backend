package com.ktds.hi.recommend.biz.domain;

/**
 * 추천 유형 열거형
 * 추천의 종류를 정의
 */
public enum RecommendType {
    TASTE_BASED("취향 기반"),
    LOCATION_BASED("위치 기반"),
    POPULARITY_BASED("인기 기반"),
    COLLABORATIVE_FILTERING("협업 필터링"),
    AI_RECOMMENDATION("AI 추천"),
    SIMILAR_USER("유사 사용자 기반");
    
    private final String description;
    
    RecommendType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
