package com.ktds.hi.recommend.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 추천 히스토리 도메인 클래스
 * 사용자의 추천 기록을 담는 도메인 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendHistory {
    
    private Long id;
    private Long memberId;
    private List<Long> recommendedStoreIds;
    private RecommendType recommendType;
    private String criteria;
    private LocalDateTime createdAt;
    
    /**
     * 추천 기준 업데이트
     */
    public RecommendHistory updateCriteria(String newCriteria) {
        return RecommendHistory.builder()
                .id(this.id)
                .memberId(this.memberId)
                .recommendedStoreIds(this.recommendedStoreIds)
                .recommendType(this.recommendType)
                .criteria(newCriteria)
                .createdAt(this.createdAt)
                .build();
    }
}
