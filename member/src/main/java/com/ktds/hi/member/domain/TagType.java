package com.ktds.hi.member.domain;

/**
 * 태그 유형 열거형
 * 취향 태그의 카테고리를 정의
 */
public enum TagType {
    CUISINE("음식 종류"),
    FLAVOR("맛"),
    DIETARY("식이 제한"),
    ATMOSPHERE("분위기"),
    PRICE("가격대");
    
    private final String description;
    
    TagType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
