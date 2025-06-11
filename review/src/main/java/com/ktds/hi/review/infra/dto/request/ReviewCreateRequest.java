package com.ktds.hi.review.infra.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 리뷰 생성 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "리뷰 생성 요청")
public class ReviewCreateRequest {
    
    @NotNull(message = "매장 ID는 필수입니다")
    @Schema(description = "매장 ID", example = "1")
    private Long storeId;
    
    @NotNull(message = "평점은 필수입니다")
    @Min(value = 1, message = "평점은 1점 이상이어야 합니다")
    @Max(value = 5, message = "평점은 5점 이하여야 합니다")
    @Schema(description = "평점 (1-5)", example = "4")
    private Integer rating;
    
    @NotBlank(message = "리뷰 내용은 필수입니다")
    @Size(min = 10, max = 1000, message = "리뷰 내용은 10자 이상 1000자 이하여야 합니다")
    @Schema(description = "리뷰 내용", example = "음식이 정말 맛있었습니다. 서비스도 친절하고 재방문 의사 있습니다.")
    private String content;
    
    @Schema(description = "이미지 URL 목록", example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]")
    private List<String> imageUrls;
    
    /**
     * 유효성 검증
     */
    public void validate() {
        if (storeId == null || storeId <= 0) {
            throw new IllegalArgumentException("유효한 매장 ID가 필요합니다");
        }
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("평점은 1점에서 5점 사이여야 합니다");
        }
        if (content == null || content.trim().length() < 10) {
            throw new IllegalArgumentException("리뷰 내용은 10자 이상이어야 합니다");
        }
    }
}
