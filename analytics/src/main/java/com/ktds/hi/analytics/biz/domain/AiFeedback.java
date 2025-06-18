package com.ktds.hi.analytics.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI 피드백 도메인 클래스
 * AI가 생성한 피드백 정보를 나타냄
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AiFeedback {
    
    private Long id;
    private Long storeId;
    private String summary;
    private List<String> positivePoints;
    private List<String> negativePoints;
    private List<String> improvementPoints;
    private List<String> recommendations;
    private String sentimentAnalysis;
    private Double confidenceScore;

    private String positiveSummary;

    private LocalDateTime generatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
