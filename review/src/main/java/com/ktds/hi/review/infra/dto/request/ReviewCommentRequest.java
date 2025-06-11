package com.ktds.hi.review.infra.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리뷰 댓글 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "리뷰 댓글 요청")
public class ReviewCommentRequest {
    
    @NotBlank(message = "댓글 내용은 필수입니다")
    @Size(min = 5, max = 500, message = "댓글 내용은 5자 이상 500자 이하여야 합니다")
    @Schema(description = "댓글 내용", example = "소중한 리뷰 감사합니다. 더 좋은 서비스로 보답하겠습니다.")
    private String content;
    
    /**
     * 유효성 검증
     */
    public void validate() {
        if (content == null || content.trim().length() < 5) {
            throw new IllegalArgumentException("댓글 내용은 5자 이상이어야 합니다");
        }
    }
}
