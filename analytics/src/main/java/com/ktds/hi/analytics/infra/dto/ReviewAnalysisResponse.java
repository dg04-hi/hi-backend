package com.ktds.hi.analytics.infra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 리뷰 분석 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewAnalysisResponse {
    
    private Long storeId;
    private Integer totalReviews;
    private Integer positiveReviewCount;
    private Integer negativeReviewCount;
    private Double positiveRate;
    private Double negativeRate;
    private LocalDate analysisDate;
}
