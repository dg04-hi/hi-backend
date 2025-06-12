package com.ktds.hi.analytics.infra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI 피드백 상세 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiFeedbackDetailResponse {
    
    private Long storeId;
    private String summary;
    private List<String> positivePoints;
    private List<String> improvementPoints;
    private List<String> recommendations;
    private String sentimentAnalysis;
    private Double confidenceScore;
    private LocalDateTime generatedAt;
}
