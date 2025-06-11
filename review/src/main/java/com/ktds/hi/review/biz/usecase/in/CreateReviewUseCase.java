package com.ktds.hi.review.biz.usecase.in;

import com.ktds.hi.review.infra.dto.request.ReviewCreateRequest;
import com.ktds.hi.review.infra.dto.response.ReviewCreateResponse;

/**
 * 리뷰 생성 유스케이스 인터페이스
 * 새로운 리뷰 작성 기능을 정의
 */
public interface CreateReviewUseCase {
    
    /**
     * 리뷰 생성
     */
    ReviewCreateResponse createReview(Long memberId, ReviewCreateRequest request);
}
