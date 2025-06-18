package com.ktds.hi.review.infra.gateway.repository;

import com.ktds.hi.review.biz.domain.ReviewStatus;
import com.ktds.hi.review.infra.gateway.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 리뷰 JPA 리포지토리 인터페이스
 * 리뷰 데이터의 CRUD 작업을 담당
 */
@Repository
public interface ReviewJpaRepository extends JpaRepository<ReviewEntity, Long> {
    
    /**
     * 매장 ID와 상태로 리뷰 목록 조회
     */
    Page<ReviewEntity> findByStoreIdAndStatus(Long storeId, ReviewStatus status, Pageable pageable);
    
    /**
     * 회원 ID와 상태로 리뷰 목록 조회
     */
    Page<ReviewEntity> findByMemberIdAndStatus(Long memberId, ReviewStatus status, Pageable pageable);
    
    /**
     * 리뷰 ID와 회원 ID로 리뷰 조회
     */
    Optional<ReviewEntity> findByIdAndMemberId(Long id, Long memberId);


    /**
     * 중복 리뷰 체크 (매장ID + 닉네임 + 내용으로 판단)
     */
    boolean existsByStoreIdAndMemberNicknameAndContent(Long storeId, String memberNickname, String content);

    /**
     * 대안: 외부 닉네임으로만 중복 체크 (더 간단한 방법)
     */
    boolean existsByStoreIdAndExternalNickname(Long storeId, String externalNickname);
}
