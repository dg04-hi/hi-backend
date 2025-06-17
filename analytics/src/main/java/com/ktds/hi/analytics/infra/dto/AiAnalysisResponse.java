package com.ktds.hi.analytics.infra.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI 분석 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI 리뷰 분석 결과")
public class AiAnalysisResponse {

	@Schema(description = "매장 ID")
	private Long storeId;

	@Schema(description = "AI 피드백 ID")
	private Long feedbackId;

	@Schema(description = "분석 요약")
	private String summary;

	@Schema(description = "긍정적 요소")
	private List<String> positivePoints;

	@Schema(description = "부정적 요소")
	private List<String> negativePoints;

	@Schema(description = "개선점")
	private List<String> improvementPoints;

	@Schema(description = "추천사항")
	private List<String> recommendations;

	@Schema(description = "감정 분석 결과")
	private String sentimentAnalysis;

	@Schema(description = "신뢰도 점수")
	private Double confidenceScore;

	@Schema(description = "분석된 리뷰 수")
	private Integer totalReviewsAnalyzed;

	@Schema(description = "생성된 실행계획")
	private List<String> actionPlans;

	@Schema(description = "분석 완료 시간")
	private LocalDateTime analyzedAt;
}