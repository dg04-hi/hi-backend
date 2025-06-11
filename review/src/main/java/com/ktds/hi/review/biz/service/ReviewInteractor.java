package com.ktds.hi.review.biz.service;

import com.ktds.hi.review.biz.usecase.in.CreateReviewUseCase;
import com.ktds.hi.review.biz.usecase.in.DeleteReviewUseCase;
import com.ktds.hi.review.biz.usecase.in.GetReviewUseCase;
import com.ktds.hi.review.biz.usecase.out.ReviewRepository;
import com.ktds.hi.review.biz.domain.Review;
import com.ktds.hi.review.biz.domain.ReviewStatus;
import com.ktds.hi.review.infra.dto.request.ReviewCreateRequest;
import com.ktds.hi.review.infra.dto.response.*;
import com.ktds.hi.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 리뷰 인터랙터 클래스
 * 리뷰 생성, 조회, 삭제 기능을 구현
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewInteractor implements CreateReviewUseCase, DeleteReviewUseCase, GetReviewUseCase {
    
    private final ReviewRepository reviewRepository;
    
    @Override
    public ReviewCreateResponse createReview(Long memberId, ReviewCreateRequest request) {
        // 리뷰 생성
        Review review = Review.builder()
                .storeId(request.getStoreId())
                .memberId(memberId)
                .memberNickname("회원" + memberId) // TODO: 회원 서비스에서 닉네임 조회
                .rating(request.getRating())
                .content(request.getContent())
                .imageUrls(request.getImageUrls())
                .status(ReviewStatus.ACTIVE)
                .likeCount(0)
                .dislikeCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Review savedReview = reviewRepository.saveReview(review);
        
        log.info("리뷰 생성 완료: reviewId={}, storeId={}, memberId={}", 
                savedReview.getId(), savedReview.getStoreId(), savedReview.getMemberId());
        
        return ReviewCreateResponse.builder()
                .reviewId(savedReview.getId())
                .message("리뷰가 성공적으로 등록되었습니다")
                .build();
    }
    
    @Override
    public ReviewDeleteResponse deleteReview(Long reviewId, Long memberId) {
        Review review = reviewRepository.findReviewByIdAndMemberId(reviewId, memberId)
                .orElseThrow(() -> new BusinessException("리뷰를 찾을 수 없거나 권한이 없습니다"));
        
        Review deletedReview = review.updateStatus(ReviewStatus.DELETED);
        reviewRepository.saveReview(deletedReview);
        
        log.info("리뷰 삭제 완료: reviewId={}, memberId={}", reviewId, memberId);
        
        return ReviewDeleteResponse.builder()
                .success(true)
                .message("리뷰가 삭제되었습니다")
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReviewListResponse> getStoreReviews(Long storeId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 20);
        Page<Review> reviews = reviewRepository.findReviewsByStoreId(storeId, pageable);
        
        return reviews.stream()
                .filter(review -> review.getStatus() == ReviewStatus.ACTIVE)
                .map(review -> ReviewListResponse.builder()
                        .reviewId(review.getId())
                        .memberNickname(review.getMemberNickname())
                        .rating(review.getRating())
                        .content(review.getContent())
                        .imageUrls(review.getImageUrls())
                        .likeCount(review.getLikeCount())
                        .dislikeCount(review.getDislikeCount())
                        .createdAt(review.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ReviewDetailResponse getReviewDetail(Long reviewId) {
        Review review = reviewRepository.findReviewById(reviewId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 리뷰입니다"));
        
        if (review.getStatus() != ReviewStatus.ACTIVE) {
            throw new BusinessException("삭제되었거나 숨겨진 리뷰입니다");
        }
        
        return ReviewDetailResponse.builder()
                .reviewId(review.getId())
                .storeId(review.getStoreId())
                .memberNickname(review.getMemberNickname())
                .rating(review.getRating())
                .content(review.getContent())
                .imageUrls(review.getImageUrls())
                .likeCount(review.getLikeCount())
                .dislikeCount(review.getDislikeCount())
                .createdAt(review.getCreatedAt())
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReviewListResponse> getMyReviews(Long memberId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 20);
        Page<Review> reviews = reviewRepository.findReviewsByMemberId(memberId, pageable);
        
        return reviews.stream()
                .filter(review -> review.getStatus() == ReviewStatus.ACTIVE)
                .map(review -> ReviewListResponse.builder()
                        .reviewId(review.getId())
                        .memberNickname(review.getMemberNickname())
                        .rating(review.getRating())
                        .content(review.getContent())
                        .imageUrls(review.getImageUrls())
                        .likeCount(review.getLikeCount())
                        .dislikeCount(review.getDislikeCount())
                        .createdAt(review.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
