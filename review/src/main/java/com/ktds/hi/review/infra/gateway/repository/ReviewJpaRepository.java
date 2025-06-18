package com.ktds.hi.review.infra.gateway.repository;

import com.ktds.hi.review.biz.domain.ReviewStatus;
import com.ktds.hi.review.infra.gateway.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    @Query("SELECT r FROM ReviewEntity r WHERE r.storeId = :storeId " +
        "AND r.status = :status " +
        "AND (CAST(:cutoffDate AS TIMESTAMP) IS NULL OR r.createdAt >= :cutoffDate) " +
        "ORDER BY r.createdAt DESC")
    Page<ReviewEntity> findRecentReviewsByStoreId(
        @Param("storeId") Long storeId,
        @Param("status") ReviewStatus status,
        @Param("cutoffDate") LocalDateTime cutoffDate,
        Pageable pageable
    );

    /**
     * 특정 매장의 닉네임별 리뷰 개수 조회 (스팸 체크용)
     */
    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.storeId = :storeId AND r.memberNickname = :memberNickname")
    Long countByStoreIdAndMemberNickname(@Param("storeId") Long storeId, @Param("memberNickname") String memberNickname);

    /**
     * 강화된 중복 체크: 매장ID + 닉네임 + 내용으로 중복 체크
     * 외부 리뷰에서 동일한 닉네임이 같은 내용의 리뷰를 작성했는지 확인
     */
    boolean existsByStoreIdAndMemberNicknameAndContent(Long storeId, String memberNickname, String content);

    /**
     * 닉네임 기반 중복 체크: 매장ID + 닉네임으로 기존 리뷰 존재 확인
     * 동일한 닉네임이 이미 리뷰를 작성했는지 체크
     */
    boolean existsByStoreIdAndMemberNickname(Long storeId, String memberNickname);

    /**
     * 시간 기반 중복 체크: 특정 시간 이후 동일한 닉네임의 리뷰 존재 확인
     * 같은 닉네임이 짧은 시간 내에 여러 리뷰를 작성했는지 체크
     */
    @Query("SELECT COUNT(r) > 0 FROM ReviewEntity r WHERE r.storeId = :storeId " +
            "AND r.memberNickname = :memberNickname AND r.createdAt >= :afterTime")
    boolean existsByStoreIdAndMemberNicknameAfterTime(
            @Param("storeId") Long storeId,
            @Param("memberNickname") String memberNickname,
            @Param("afterTime") LocalDateTime afterTime);

    /**
     * 컨텐츠 유사도 체크: 유사한 길이의 리뷰 존재 확인
     * 동일한 닉네임이 비슷한 길이의 리뷰를 작성했는지 체크 (간단한 유사도 체크)
     */
    @Query("SELECT COUNT(r) > 0 FROM ReviewEntity r WHERE r.storeId = :storeId " +
            "AND r.memberNickname = :memberNickname " +
            "AND LENGTH(r.content) BETWEEN :minLength AND :maxLength")
    boolean existsBySimilarContentLength(
            @Param("storeId") Long storeId,
            @Param("memberNickname") String memberNickname,
            @Param("minLength") int minLength,
            @Param("maxLength") int maxLength);
    /**
     * 리뷰 ID와 회원 ID로 리뷰 조회
     */
    Optional<ReviewEntity> findByIdAndMemberId(Long id, Long memberId);


    /**
     * ✅ 수정: 매장ID + 내용으로 중복 리뷰 체크
     */
    boolean existsByStoreIdAndContent(Long storeId, String content);

    /**
     * 대안: 외부 닉네임으로만 중복 체크 (더 간단한 방법)
     */
//    boolean existsByStoreIdAndExternalNickname(Long storeId, String externalNickname);
}
