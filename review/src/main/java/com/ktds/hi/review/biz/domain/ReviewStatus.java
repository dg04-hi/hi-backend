package com.ktds.hi.review.biz.domain;

/**
 * 리뷰 상태 열거형
 * 리뷰의 현재 상태를 정의
 */
public enum ReviewStatus {
    ACTIVE("활성"),
    DELETED("삭제됨"),
    HIDDEN("숨김"),
    REPORTED("신고됨");
    
    private final String description;
    
    ReviewStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
