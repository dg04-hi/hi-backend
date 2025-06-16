package com.ktds.hi.member.domain;

import java.util.Arrays;

/**
 * 태그 유형 열거형
 * 취향 태그의 카테고리를 정의
 */
public enum TagType {
    TASTE("맛"),
    ATMOSPHERE("분위기"),
    ALLERGY("알러지"),
    SERVICE("서비스"),
    PRICE_RANGE("가격대"),
    CUISINE_TYPE("음식 종류"),
    HEALTH_INFO("건강 정보");

    private final String description;

    TagType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 설명으로 TagType 찾기
     */
    public static TagType fromDescription(String description) {
        for (TagType type : TagType.values()) {
            if (type.description.equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown tag type description: " + description);
    }

    /**
     * 모든 태그 타입 설명 목록 반환
     */
    public static String[] getAllDescriptions() {
        return Arrays.stream(TagType.values())
                .map(TagType::getDescription)
                .toArray(String[]::new);
    }
}