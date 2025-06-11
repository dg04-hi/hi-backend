package com.ktds.hi.review.infra.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리뷰 반응 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "리뷰 반응 응답")
public class ReviewReactionResponse {
    
    @Schema(description = "성공 여부")
    private Boolean success;
    
    @Schema(description = "응답 메시지")
    private String message;
}
