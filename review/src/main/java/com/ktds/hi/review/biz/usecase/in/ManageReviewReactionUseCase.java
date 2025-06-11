package com.ktds.hi.review.biz.usecase.in;

import com.ktds.hi.review.biz.domain.ReactionType;
import com.ktds.hi.review.infra.dto.response.ReviewReactionResponse;

/**
 * 리뷰 반응 관리 유스케이스 인터페이스
 * 리뷰 좋아요/싫어요 기능을 정의
 */
public interface ManageReviewReactionUseCase {
    
    /**
     * 리뷰 반응 추가
     */
    ReviewReactionResponse addReaction(Long reviewId, Long memberId, ReactionType reactionType);
    
    /**
     * 리뷰 반응 제거
     */
    ReviewReactionResponse removeReaction(Long reviewId, Long memberId);
}
