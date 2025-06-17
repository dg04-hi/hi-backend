package com.ktds.hi.analytics.infra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AI 피드백 상세 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiFeedbackDetailResponse {

    private Long feedbackId;
    private Long storeId;
    private String summary;
    private List<String> positivePoints;
    private List<String> improvementPoints;
    private List<String> existActionPlan; // improvemnetPoints 중에서 처리 된것.
    private List<String> recommendations;
    private String sentimentAnalysis;
    private Double confidenceScore;
    private LocalDateTime generatedAt;


    public void updateImprovementCheck(List<String> actionPlanTitle){
        Set<String> trimmedTitles = actionPlanTitle.stream()
            .map(String::trim)
            .collect(Collectors.toSet());

        this.existActionPlan =
            improvementPoints.stream()
                .map(String::trim)
                .filter(point -> trimmedTitles.stream()
                    .anyMatch(title -> title.contains(point)))
                .toList();
    }
}
