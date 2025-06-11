package com.ktds.hi.review.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 리뷰 댓글 도메인 클래스
 * 리뷰에 대한 점주 댓글 정보를 담는 도메인 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewComment {
    
    private Long id;
    private Long reviewId;
    private Long ownerId;
    private String ownerNickname;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 댓글 내용 수정
     */
    public ReviewComment updateContent(String newContent) {
        return ReviewComment.builder()
                .id(this.id)
                .reviewId(this.reviewId)
                .ownerId(this.ownerId)
                .ownerNickname(this.ownerNickname)
                .content(newContent)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
