package com.ktds.hi.analytics.infra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI 피드백 요약 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiFeedbackSummaryResponse {
    
    private Long storeId;
    private Boolean hasData;
    private String message;
    private Double overallScore;
    private String keyInsight;
    private String priorityRecommendation;
    private LocalDateTime lastUpdated;
}
