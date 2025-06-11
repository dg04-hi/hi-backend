package com.ktds.hi.review.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 리뷰 반응 도메인 클래스
 * 리뷰에 대한 좋아요/싫어요 반응 정보를 담는 도메인 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReaction {
    
    private Long id;
    private Long reviewId;
    private Long memberId;
    private ReactionType reactionType;
    private LocalDateTime createdAt;
    
    /**
     * 반응 유형 변경
     */
    public ReviewReaction updateReactionType(ReactionType newType) {
        return ReviewReaction.builder()
                .id(this.id)
                .reviewId(this.reviewId)
                .memberId(this.memberId)
                .reactionType(newType)
                .createdAt(this.createdAt)
                .build();
    }
}
