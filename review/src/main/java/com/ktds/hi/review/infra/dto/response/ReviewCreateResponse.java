package com.ktds.hi.review.infra.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리뷰 생성 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "리뷰 생성 응답")
public class ReviewCreateResponse {
    
    @Schema(description = "생성된 리뷰 ID")
    private Long reviewId;
    
    @Schema(description = "응답 메시지")
    private String message;
}
