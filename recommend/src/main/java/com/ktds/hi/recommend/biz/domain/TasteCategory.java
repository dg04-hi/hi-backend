package com.ktds.hi.recommend.biz.domain;

/**
 * 취향 카테고리 열거형
 * 음식 카테고리를 정의
 */
public enum TasteCategory {
    KOREAN("한식"),
    CHINESE("중식"),
    JAPANESE("일식"),
    WESTERN("양식"),
    FAST_FOOD("패스트푸드"),
    CAFE("카페"),
    DESSERT("디저트"),
    CHICKEN("치킨"),
    PIZZA("피자"),
    ASIAN("아시안"),
    VEGETARIAN("채식"),
    SEAFOOD("해산물");
    
    private final String description;
    
    TasteCategory(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
