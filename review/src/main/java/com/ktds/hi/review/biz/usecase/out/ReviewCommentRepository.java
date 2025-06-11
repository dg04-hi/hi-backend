package com.ktds.hi.review.biz.usecase.out;

import com.ktds.hi.review.biz.domain.ReviewComment;

import java.util.List;
import java.util.Optional;

/**
 * 리뷰 댓글 리포지토리 인터페이스
 * 리뷰 댓글 데이터 영속성 기능을 정의
 */
public interface ReviewCommentRepository {
    
    /**
     * 댓글 저장
     */
    ReviewComment saveComment(ReviewComment comment);
    
    /**
     * 리뷰 ID로 댓글 목록 조회
     */
    List<ReviewComment> findCommentsByReviewId(Long reviewId);
    
    /**
     * 댓글 ID로 조회
     */
    Optional<ReviewComment> findCommentById(Long commentId);
    
    /**
     * 댓글 삭제
     */
    void deleteComment(Long commentId);
    
    /**
     * 댓글 ID와 소유자 ID로 댓글 조회
     */
    Optional<ReviewComment> findCommentByIdAndOwnerId(Long commentId, Long ownerId);
}
