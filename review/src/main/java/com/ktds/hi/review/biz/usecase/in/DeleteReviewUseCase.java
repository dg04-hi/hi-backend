package com.ktds.hi.review.biz.usecase.in;

import com.ktds.hi.review.infra.dto.response.ReviewDeleteResponse;

/**
 * 리뷰 삭제 유스케이스 인터페이스
 * 리뷰 삭제 기능을 정의
 */
public interface DeleteReviewUseCase {
    
    /**
     * 리뷰 삭제
     */
    ReviewDeleteResponse deleteReview(Long reviewId, Long memberId);
}
