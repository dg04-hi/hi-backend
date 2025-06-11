package com.ktds.hi.review.infra.gateway;

import com.ktds.hi.review.biz.usecase.out.ReviewReactionRepository;
import com.ktds.hi.review.biz.domain.ReviewReaction;
import com.ktds.hi.review.biz.domain.ReactionType;
import com.ktds.hi.review.infra.gateway.repository.ReviewReactionJpaRepository;
import com.ktds.hi.review.infra.gateway.entity.ReviewReactionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 리뷰 반응 리포지토리 어댑터 클래스
 * 도메인 리포지토리 인터페이스를 JPA 리포지토리에 연결
 */
@Component
@RequiredArgsConstructor
public class ReviewReactionRepositoryAdapter implements ReviewReactionRepository {
    
    private final ReviewReactionJpaRepository reviewReactionJpaRepository;
    
    @Override
    public ReviewReaction saveReaction(ReviewReaction reaction) {
        ReviewReactionEntity entity = toEntity(reaction);
        ReviewReactionEntity savedEntity = reviewReactionJpaRepository.save(entity);
        return toDomain(savedEntity);
    }
    
    @Override
    public Optional<ReviewReaction> findReactionByReviewIdAndMemberId(Long reviewId, Long memberId) {
        return reviewReactionJpaRepository.findByReviewIdAndMemberId(reviewId, memberId)
                .map(this::toDomain);
    }
    
    @Override
    public void deleteReaction(Long reactionId) {
        reviewReactionJpaRepository.deleteById(reactionId);
    }
    
    @Override
    public Long countReactionsByReviewIdAndType(Long reviewId, ReactionType reactionType) {
        return reviewReactionJpaRepository.countByReviewIdAndReactionType(reviewId, reactionType);
    }
    
    /**
     * 엔티티를 도메인으로 변환
     */
    private ReviewReaction toDomain(ReviewReactionEntity entity) {
        return ReviewReaction.builder()
                .id(entity.getId())
                .reviewId(entity.getReviewId())
                .memberId(entity.getMemberId())
                .reactionType(entity.getReactionType())
                .createdAt(entity.getCreatedAt())
                .build();
    }
    
    /**
     * 도메인을 엔티티로 변환
     */
    private ReviewReactionEntity toEntity(ReviewReaction domain) {
        return ReviewReactionEntity.builder()
                .id(domain.getId())
                .reviewId(domain.getReviewId())
                .memberId(domain.getMemberId())
                .reactionType(domain.getReactionType())
                .build();
    }
}
