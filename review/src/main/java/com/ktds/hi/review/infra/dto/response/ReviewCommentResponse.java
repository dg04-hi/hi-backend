package com.ktds.hi.review.infra.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 리뷰 댓글 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "리뷰 댓글 응답")
public class ReviewCommentResponse {
    
    @Schema(description = "댓글 ID")
    private Long commentId;
    
    @Schema(description = "점주 닉네임")
    private String ownerNickname;
    
    @Schema(description = "댓글 내용")
    private String content;
    
    @Schema(description = "작성일시")
    private LocalDateTime createdAt;
}
