package com.ktds.hi.recommend.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 취향 프로필 도메인 클래스
 * 사용자의 취향 분석 결과를 담는 도메인 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TasteProfile {
    
    private Long id;
    private Long memberId;
    private List<TasteCategory> preferredCategories;
    private Map<String, Double> categoryScores;
    private List<String> preferredTags;
    private Map<String, Object> behaviorPatterns;
    private Double pricePreference;
    private Double distancePreference;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 취향 프로필 업데이트
     */
    public TasteProfile updateProfile(List<TasteCategory> categories, Map<String, Double> scores,
                                    List<String> tags, Map<String, Object> patterns,
                                    Double pricePreference, Double distancePreference) {
        return TasteProfile.builder()
                .id(this.id)
                .memberId(this.memberId)
                .preferredCategories(categories)
                .categoryScores(scores)
                .preferredTags(tags)
                .behaviorPatterns(patterns)
                .pricePreference(pricePreference)
                .distancePreference(distancePreference)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
