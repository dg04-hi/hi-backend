package com.ktds.hi.recommend.biz.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class TasteProfile {
    private Long id;
    private Long memberId;
    private List<String> cuisinePreferences;
    private String priceRange;
    private Integer distancePreference;
    private List<String> tasteTags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}