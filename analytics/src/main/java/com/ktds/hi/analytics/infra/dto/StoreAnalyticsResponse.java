package com.ktds.hi.analytics.infra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 매장 분석 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreAnalyticsResponse {
    
    private Long storeId;
    private Integer totalReviews;
    private Double averageRating;
    private Double sentimentScore;
    private Double positiveReviewRate;
    private Double negativeReviewRate;
    private LocalDateTime lastAnalysisDate;
}
