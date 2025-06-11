package com.ktds.hi.review.biz.domain;

/**
 * 반응 유형 열거형
 * 리뷰 반응의 종류를 정의
 */
public enum ReactionType {
    LIKE("좋아요"),
    DISLIKE("싫어요");
    
    private final String description;
    
    ReactionType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
