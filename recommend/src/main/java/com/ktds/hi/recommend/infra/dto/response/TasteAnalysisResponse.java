package com.ktds.hi.recommend.infra.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 취향 분석 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "취향 분석 응답")
public class TasteAnalysisResponse {
    
    @Schema(description = "회원 ID")
    private Long memberId;
    
    @Schema(description = "선호 카테고리")
    private List<String> preferredCategories;
    
    @Schema(description = "최고 선호 카테고리")
    private String topCategory;
    
    @Schema(description = "카테고리별 점수")
    private Map<String, Double> categoryScores;
    
    @Schema(description = "선호 태그")
    private List<String> preferredTags;
    
    @Schema(description = "가격 선호도")
    private Double pricePreference;
    
    @Schema(description = "거리 선호도")
    private Double distancePreference;
    
    @Schema(description = "분석 일시")
    private LocalDateTime analysisDate;
}
