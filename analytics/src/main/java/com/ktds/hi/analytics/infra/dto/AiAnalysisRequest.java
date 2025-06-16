package com.ktds.hi.analytics.infra.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * AI 분석 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI 리뷰 분석 요청")
public class AiAnalysisRequest {

	@Schema(description = "분석할 최근 일수", example = "30")
	@Min(value = 1, message = "분석 기간은 최소 1일 이상이어야 합니다")
	@Max(value = 365, message = "분석 기간은 최대 365일까지 가능합니다")
	@Builder.Default
	private Integer days = 30;

	@Schema(description = "실행계획 자동 생성 여부", example = "false")
	@Builder.Default
	private Boolean generateActionPlan = false;
}
