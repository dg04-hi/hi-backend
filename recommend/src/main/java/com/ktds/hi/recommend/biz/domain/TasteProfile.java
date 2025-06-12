package com.ktds.hi.recommend.biz.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class TasteProfile {
    private Long id;
    private Long memberId;
    private List<String> cuisinePreferences;  // 기존 유지
    private String priceRange;               // 기존 유지
    private Integer distancePreference;      // 기존 유지
    private List<String> tasteTags;         // 기존 유지

    // 추가 필드들 (Adapter에서 사용)
    private List<TasteCategory> preferredCategories;
    private Map<String, Double> categoryScores;
    private List<String> preferredTags;
    private Map<String, Object> behaviorPatterns;
    private Double pricePreference;
    private Double distancePreferenceDouble; // distancePreference와 구분

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}