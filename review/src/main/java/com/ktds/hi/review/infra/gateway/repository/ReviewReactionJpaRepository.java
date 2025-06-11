package com.ktds.hi.review.infra.gateway.repository;

import com.ktds.hi.review.biz.domain.ReactionType;
import com.ktds.hi.review.infra.gateway.entity.ReviewReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 리뷰 반응 JPA 리포지토리 인터페이스
 * 리뷰 반응 데이터의 CRUD 작업을 담당
 */
@Repository
public interface ReviewReactionJpaRepository extends JpaRepository<ReviewReactionEntity, Long> {
    
    /**
     * 리뷰 ID와 회원 ID로 반응 조회
     */
    Optional<ReviewReactionEntity> findByReviewIdAndMemberId(Long reviewId, Long memberId);
    
    /**
     * 리뷰 ID와 반응 유형별 개수 조회
     */
    Long countByReviewIdAndReactionType(Long reviewId, ReactionType reactionType);
    
    /**
     * 회원 ID로 반응 목록 조회
     */
    Long countByMemberId(Long memberId);
}
