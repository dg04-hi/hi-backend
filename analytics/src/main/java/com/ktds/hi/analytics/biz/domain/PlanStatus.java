package com.ktds.hi.analytics.biz.domain;

/**
 * 실행 계획 상태 열거형
 */
public enum PlanStatus {
    PLANNED("계획됨"),
    IN_PROGRESS("진행중"),
    COMPLETED("완료됨"),
    CANCELLED("취소됨");
    
    private final String description;
    
    PlanStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
