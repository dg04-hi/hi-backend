package com.ktds.hi.recommend.biz.domain;

public enum TasteCategory {
    KOREAN("한식"),
    CHINESE("중식"),
    JAPANESE("일식"),
    WESTERN("양식"),
    CAFE("카페"),
    FASTFOOD("패스트푸드"),
    DESSERT("디저트"),
    CHICKEN("치킨"),
    PIZZA("피자"),
    OTHER("기타");

    private final String description;

    TasteCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}