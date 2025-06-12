package com.ktds.hi.recommend.biz.domain;

public enum RecommendType {
    AI_RECOMMENDATION("AI 추천"),
    LOCATION_BASED("위치 기반"),
    POPULARITY_BASED("인기 기반"),
    COLLABORATIVE_FILTERING("협업 필터링"),
    PREFERENCE_BASED("취향 기반");

    private final String description;

    RecommendType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}