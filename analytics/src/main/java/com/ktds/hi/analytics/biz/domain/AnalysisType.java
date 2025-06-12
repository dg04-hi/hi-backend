package com.ktds.hi.analytics.biz.domain;

/**
 * 분석 유형 열거형
 */
public enum AnalysisType {
    FULL_ANALYSIS("전체 분석"),
    INCREMENTAL_ANALYSIS("증분 분석"),
    SENTIMENT_ANALYSIS("감정 분석"),
    TREND_ANALYSIS("트렌드 분석");
    
    private final String description;
    
    AnalysisType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
