package com.ktds.hi.review.infra.gateway.repository;

import com.ktds.hi.review.infra.gateway.entity.ReviewCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 리뷰 댓글 JPA 리포지토리 인터페이스
 * 리뷰 댓글 데이터의 CRUD 작업을 담당
 */
@Repository
public interface ReviewCommentJpaRepository extends JpaRepository<ReviewCommentEntity, Long> {
    
    /**
     * 리뷰 ID로 댓글 목록 조회 (최신순)
     */
    List<ReviewCommentEntity> findByReviewIdOrderByCreatedAtDesc(Long reviewId);
    
    /**
     * 댓글 ID와 소유자 ID로 댓글 조회
     */
    Optional<ReviewCommentEntity> findByIdAndOwnerId(Long id, Long ownerId);
    
    /**
     * 소유자 ID로 댓글 목록 조회
     */
    List<ReviewCommentEntity> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
}
