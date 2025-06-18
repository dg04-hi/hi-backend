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

    // 🔥 긍정적인 리뷰만 조회하는 메서드 추가
    /**
     * 긍정적인 리뷰만 조회 (평점 4점 이상)
     * @param storeId 매장 ID
     * @param days 조회 기간 (일)
     * @return 긍정적인 리뷰 목록
     */
    List<String> getPositiveReviews(Long storeId, Integer days);
    
    /**
     * 리뷰 개수 조회
     */
    Integer getReviewCount(Long storeId);
    
    /**
     * 평균 평점 조회
     */
    Double getAverageRating(Long storeId);
}
