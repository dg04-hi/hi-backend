package com.ktds.hi.review.infra.gateway;

import com.ktds.hi.review.biz.usecase.out.ReviewRepository;
import com.ktds.hi.review.biz.domain.Review;
import com.ktds.hi.review.biz.domain.ReviewStatus;
import com.ktds.hi.review.infra.gateway.repository.ReviewJpaRepository;
import com.ktds.hi.review.infra.gateway.entity.ReviewEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 리뷰 리포지토리 어댑터 클래스
 * 도메인 리포지토리 인터페이스를 JPA 리포지토리에 연결
 */
@Component
@RequiredArgsConstructor
public class ReviewRepositoryAdapter implements ReviewRepository {
    
    private final ReviewJpaRepository reviewJpaRepository;
    
    @Override
    public Review saveReview(Review review) {
        ReviewEntity entity = toEntity(review);
        ReviewEntity savedEntity = reviewJpaRepository.save(entity);
        return toDomain(savedEntity);
    }
    
    @Override
    public Optional<Review> findReviewById(Long reviewId) {
        return reviewJpaRepository.findById(reviewId)
                .map(this::toDomain);
    }
    
    @Override
    public Page<Review> findReviewsByStoreId(Long storeId, Pageable pageable) {
        Page<ReviewEntity> entities = reviewJpaRepository.findByStoreIdAndStatus(storeId, ReviewStatus.ACTIVE, pageable);
        return entities.map(this::toDomain);
    }

    @Override
    public Page<Review> findReviewsByStoreIdOrderByCreatedAtDesc(Long storeId, Pageable pageable) {
        Page<ReviewEntity> entities = reviewJpaRepository.findByStoreIdAndStatus(storeId, ReviewStatus.ACTIVE,
            pageable);
        return entities.map(this::toDomain);
    }

    @Override
    public Page<Review> findRecentReviewsByStoreId(Long storeId, Pageable pageable,
        LocalDateTime cutoffDate) {

        Page<ReviewEntity> entities = reviewJpaRepository.findRecentReviewsByStoreId(storeId, ReviewStatus.ACTIVE,
            cutoffDate,pageable);
        return entities.map(this::toDomain);

    }

    @Override
    public Page<Review> findReviewsByMemberId(Long memberId, Pageable pageable) {
        Page<ReviewEntity> entities = reviewJpaRepository.findByMemberIdAndStatus(memberId, ReviewStatus.ACTIVE, pageable);
        return entities.map(this::toDomain);
    }
    
    @Override
    public void deleteReview(Long reviewId) {
        reviewJpaRepository.deleteById(reviewId);
    }
    
    @Override
    public Optional<Review> findReviewByIdAndMemberId(Long reviewId, Long memberId) {
        return reviewJpaRepository.findByIdAndMemberId(reviewId, memberId)
                .map(this::toDomain);
    }
    
    /**
     * 엔티티를 도메인으로 변환
     */
    private Review toDomain(ReviewEntity entity) {
        return Review.builder()
                .id(entity.getId())
                .storeId(entity.getStoreId())
                .memberId(entity.getMemberId())
                .memberNickname(entity.getMemberNickname())
                .rating(entity.getRating())
                .content(entity.getContent())
                .imageUrls(entity.getImageUrls())
                .status(entity.getStatus())
                .likeCount(entity.getLikeCount())
                .dislikeCount(entity.getDislikeCount())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    /**
     * 도메인을 엔티티로 변환
     */
    private ReviewEntity toEntity(Review domain) {
        return ReviewEntity.builder()
                .id(domain.getId())
                .storeId(domain.getStoreId())
                .memberId(domain.getMemberId())
                .memberNickname(domain.getMemberNickname())
                .rating(domain.getRating())
                .content(domain.getContent())
                .imageUrls(domain.getImageUrls())
                .status(domain.getStatus())
                .likeCount(domain.getLikeCount())
                .dislikeCount(domain.getDislikeCount())
                .build();
    }
}
