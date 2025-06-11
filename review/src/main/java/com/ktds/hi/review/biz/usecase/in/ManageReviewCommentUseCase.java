package com.ktds.hi.review.biz.usecase.in;

import com.ktds.hi.review.infra.dto.request.ReviewCommentRequest;
import com.ktds.hi.review.infra.dto.response.ReviewCommentResponse;

import java.util.List;

/**
 * 리뷰 댓글 관리 유스케이스 인터페이스
 * 리뷰 댓글 작성, 조회, 삭제 기능을 정의
 */
public interface ManageReviewCommentUseCase {
    
    /**
     * 리뷰 댓글 목록 조회
     */
    List<ReviewCommentResponse> getReviewComments(Long reviewId);
    
    /**
     * 리뷰 댓글 작성 (점주용)
     */
    ReviewCommentResponse createComment(Long reviewId, Long ownerId, ReviewCommentRequest request);
    
    /**
     * 리뷰 댓글 삭제
     */
    void deleteComment(Long commentId, Long ownerId);
}
