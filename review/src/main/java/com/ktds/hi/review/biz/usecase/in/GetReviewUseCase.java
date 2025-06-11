package com.ktds.hi.review.biz.usecase.in;

import com.ktds.hi.review.infra.dto.response.ReviewDetailResponse;
import com.ktds.hi.review.infra.dto.response.ReviewListResponse;

import java.util.List;

/**
 * 리뷰 조회 유스케이스 인터페이스
 * 리뷰 목록 및 상세 조회 기능을 정의
 */
public interface GetReviewUseCase {
    
    /**
     * 매장 리뷰 목록 조회
     */
    List<ReviewListResponse> getStoreReviews(Long storeId, Integer page, Integer size);
    
    /**
     * 리뷰 상세 조회
     */
    ReviewDetailResponse getReviewDetail(Long reviewId);
    
    /**
     * 내가 작성한 리뷰 목록 조회
     */
    List<ReviewListResponse> getMyReviews(Long memberId, Integer page, Integer size);
}
