package com.ktds.hi.store.domain;


/**
 * 태그 카테고리 열거형 클래스
 * 매장 태그의 분류를 정의
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
public enum TagCategory {
    TASTE("맛"),           // 매운맛, 단맛, 짠맛 등
    ATMOSPHERE("분위기"),   // 깨끗한, 혼밥, 데이트 등
    ALLERGY("알러지"),      // 유제품, 견과류, 갑각류 등
    SERVICE("서비스"),      // 빠른서비스, 친절한, 조용한 등
    PRICE("가격대"),        // 저렴한, 합리적인, 가성비 등
    FOOD_TYPE("음식 종류"), // 한식, 중식, 일식 등
    HEALTH("건강 정보");    // 저염, 저당, 글루텐프리 등

    private final String description;

    TagCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}