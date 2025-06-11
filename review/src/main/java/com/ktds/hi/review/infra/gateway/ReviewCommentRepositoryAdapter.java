package com.ktds.hi.review.infra.gateway;

import com.ktds.hi.review.biz.usecase.out.ReviewCommentRepository;
import com.ktds.hi.review.biz.domain.ReviewComment;
import com.ktds.hi.review.infra.gateway.repository.ReviewCommentJpaRepository;
import com.ktds.hi.review.infra.gateway.entity.ReviewCommentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 리뷰 댓글 리포지토리 어댑터 클래스
 * 도메인 리포지토리 인터페이스를 JPA 리포지토리에 연결
 */
@Component
@RequiredArgsConstructor
public class ReviewCommentRepositoryAdapter implements ReviewCommentRepository {
    
    private final ReviewCommentJpaRepository reviewCommentJpaRepository;
    
    @Override
    public ReviewComment saveComment(ReviewComment comment) {
        ReviewCommentEntity entity = toEntity(comment);
        ReviewCommentEntity savedEntity = reviewCommentJpaRepository.save(entity);
        return toDomain(savedEntity);
    }
    
    @Override
    public List<ReviewComment> findCommentsByReviewId(Long reviewId) {
        List<ReviewCommentEntity> entities = reviewCommentJpaRepository.findByReviewIdOrderByCreatedAtDesc(reviewId);
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<ReviewComment> findCommentById(Long commentId) {
        return reviewCommentJpaRepository.findById(commentId)
                .map(this::toDomain);
    }
    
    @Override
    public void deleteComment(Long commentId) {
        reviewCommentJpaRepository.deleteById(commentId);
    }
    
    @Override
    public Optional<ReviewComment> findCommentByIdAndOwnerId(Long commentId, Long ownerId) {
        return reviewCommentJpaRepository.findByIdAndOwnerId(commentId, ownerId)
                .map(this::toDomain);
    }
    
    /**
     * 엔티티를 도메인으로 변환
     */
    private ReviewComment toDomain(ReviewCommentEntity entity) {
        return ReviewComment.builder()
                .id(entity.getId())
                .reviewId(entity.getReviewId())
                .ownerId(entity.getOwnerId())
                .ownerNickname(entity.getOwnerNickname())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    /**
     * 도메인을 엔티티로 변환
     */
    private ReviewCommentEntity toEntity(ReviewComment domain) {
        return ReviewCommentEntity.builder()
                .id(domain.getId())
                .reviewId(domain.getReviewId())
                .ownerId(domain.getOwnerId())
                .ownerNickname(domain.getOwnerNickname())
                .content(domain.getContent())
                .build();
    }
}
