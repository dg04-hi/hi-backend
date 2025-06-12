package com.ktds.hi.analytics.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 분석 도메인 클래스
 * 매장의 전반적인 분석 데이터를 나타냄
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Analytics {
    
    private Long id;
    private Long storeId;
    private Integer totalReviews;
    private Double averageRating;
    private Double sentimentScore;
    private Double positiveReviewRate;
    private Double negativeReviewRate;
    private LocalDateTime lastAnalysisDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
