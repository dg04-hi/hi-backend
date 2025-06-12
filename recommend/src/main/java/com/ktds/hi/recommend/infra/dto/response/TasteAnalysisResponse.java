package com.ktds.hi.recommend.infra.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 취향 분석 결과 응답 DTO
 */
@Getter
@Builder
@Schema(description = "취향 분석 결과 응답")
public class TasteAnalysisResponse {

    @Schema(description = "회원 ID", example = "1")
    private Long memberId;

    @Schema(description = "선호 카테고리 목록", example = "[\"한식\", \"일식\"]")
    private List<String> preferredCategories;

    @Schema(description = "최고 선호 카테고리", example = "한식")
    private String topCategory;

    @Schema(description = "카테고리별 점수")
    private Map<String, Double> categoryScores;

    @Schema(description = "선호 태그 목록", example = "[\"매운맛\", \"혼밥\"]")
    private List<String> preferredTags;

    @Schema(description = "분석 일시")
    private LocalDateTime analysisDate;

    @Schema(description = "신뢰도", example = "0.75")
    private Double confidence;

    @Schema(description = "추천사항", example = "[\"한식 카테고리의 새로운 매장을 시도해보세요\"]")
    private List<String> recommendations;
}
