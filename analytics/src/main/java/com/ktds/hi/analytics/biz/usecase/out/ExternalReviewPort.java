package com.ktds.hi.analytics.biz.usecase.out;

import java.util.List;

/**
 * 외부 리뷰 데이터 포트 인터페이스
 * 리뷰 서비스와의 연동을 위한 출력 포트
 */
public interface ExternalReviewPort {
    
    /**
     * 매장의 리뷰 데이터 조회
     */
    List<String> getReviewData(Long storeId);
    
    /**
     * 최근 리뷰 데이터 조회
     */
    List<String> getRecentReviews(Long storeId, Integer days);
    
    /**
     * 리뷰 개수 조회
     */
    Integer getReviewCount(Long storeId);
    
    /**
     * 평균 평점 조회
     */
    Double getAverageRating(Long storeId);
}
