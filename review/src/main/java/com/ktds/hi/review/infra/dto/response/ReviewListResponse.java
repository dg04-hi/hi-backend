package com.ktds.hi.review.infra.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 리뷰 목록 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "리뷰 목록 응답")
public class ReviewListResponse {
    
    @Schema(description = "리뷰 ID")
    private Long reviewId;
    
    @Schema(description = "작성자 닉네임")
    private String memberNickname;
    
    @Schema(description = "평점")
    private Integer rating;
    
    @Schema(description = "리뷰 내용")
    private String content;
    
    @Schema(description = "이미지 URL 목록")
    private List<String> imageUrls;
    
    @Schema(description = "좋아요 수")
    private Integer likeCount;
    
    @Schema(description = "싫어요 수")
    private Integer dislikeCount;
    
    @Schema(description = "작성일시")
    private LocalDateTime createdAt;
}
