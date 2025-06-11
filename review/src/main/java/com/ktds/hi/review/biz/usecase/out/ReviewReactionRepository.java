package com.ktds.hi.review.biz.usecase.out;

import com.ktds.hi.review.biz.domain.ReviewReaction;
import com.ktds.hi.review.biz.domain.ReactionType;

import java.util.Optional;

/**
 * 리뷰 반응 리포지토리 인터페이스
 * 리뷰 반응 데이터 영속성 기능을 정의
 */
public interface ReviewReactionRepository {
    
    /**
     * 반응 저장
     */
    ReviewReaction saveReaction(ReviewReaction reaction);
    
    /**
     * 리뷰 ID와 회원 ID로 반응 조회
     */
    Optional<ReviewReaction> findReactionByReviewIdAndMemberId(Long reviewId, Long memberId);
    
    /**
     * 반응 삭제
     */
    void deleteReaction(Long reactionId);
    
    /**
     * 리뷰 ID와 반응 유형별 개수 조회
     */
    Long countReactionsByReviewIdAndType(Long reviewId, ReactionType reactionType);
}
