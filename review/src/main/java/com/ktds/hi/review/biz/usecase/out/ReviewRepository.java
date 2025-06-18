package com.ktds.hi.review.biz.usecase.out;

import com.ktds.hi.review.biz.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 리뷰 리포지토리 인터페이스
 * 리뷰 데이터 영속성 기능을 정의
 */
public interface ReviewRepository {
    
    /**
     * 리뷰 저장
     */
    Review saveReview(Review review);
    
    /**
     * 리뷰 ID로 조회
     */
    Optional<Review> findReviewById(Long reviewId);
    
    /**
     * 매장 ID로 리뷰 목록 조회
     */
    Page<Review> findReviewsByStoreId(Long storeId, Pageable pageable);


    /**
     * 매장 ID로 리뷰 목록 조회
     */
    Page<Review> findReviewsByStoreIdOrderByCreatedAtDesc(Long storeId, Pageable pageable);

    /**
     * 회원 ID로 리뷰 목록 조회
     */
    Page<Review> findReviewsByMemberId(Long memberId, Pageable pageable);
    
    /**
     * 리뷰 삭제
     */
    void deleteReview(Long reviewId);
    
    /**
     * 리뷰 ID와 회원 ID로 리뷰 조회
     */
    Optional<Review> findReviewByIdAndMemberId(Long reviewId, Long memberId);
}
