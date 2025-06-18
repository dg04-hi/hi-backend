package com.ktds.hi.analytics.infra.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 고객용 긍정 리뷰 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "고객용 긍정 리뷰 응답")
public class CustomerPositiveReviewResponse {

	@Schema(description = "매장 ID")
	private Long storeId;

	@Schema(description = "긍정적인 리뷰 요약")
	private String positiveSummary;


	@Schema(description = "분석된 총 리뷰 수")
	private Integer totalReviewsAnalyzed;

	@Schema(description = "분석 일시")
	private LocalDateTime analyzedAt;
}

