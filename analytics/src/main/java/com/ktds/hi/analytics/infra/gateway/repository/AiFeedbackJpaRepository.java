package com.ktds.hi.analytics.infra.gateway.repository;

import com.ktds.hi.analytics.infra.gateway.entity.AiFeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * AI 피드백 JPA 리포지토리 인터페이스
 * AI 피드백 데이터의 CRUD 작업을 담당
 */
@Repository
public interface AiFeedbackJpaRepository extends JpaRepository<AiFeedbackEntity, Long> {
    
    /**
     * 매장 ID로 AI 피드백 조회 (최신순)
     */
    Optional<AiFeedbackEntity> findByStoreId(Long storeId);

    /**
     * 매장 ID로 최신 AI 피드백 조회
     */
    @Query("SELECT af FROM AiFeedbackEntity af WHERE af.storeId = :storeId ORDER BY af.id DESC LIMIT 1")
    Optional<AiFeedbackEntity> findLatestByStoreId(@Param("storeId") Long storeId);

    /**
     * 특정 기간 이후 생성된 AI 피드백 조회
     */
    @Query("SELECT af FROM AiFeedbackEntity af WHERE af.generatedAt >= :afterDate ORDER BY af.generatedAt DESC")
    List<AiFeedbackEntity> findByGeneratedAtAfter(@Param("afterDate") LocalDateTime afterDate);
    
    /**
     * 신뢰도가 특정 값 이상인 AI 피드백 조회
     */
    @Query("SELECT af FROM AiFeedbackEntity af WHERE af.confidenceScore >= :score ORDER BY af.confidenceScore DESC")
    List<AiFeedbackEntity> findByHighConfidenceScore(@Param("score") Double score);
    
    /**
     * 매장별 AI 피드백 개수 조회
     */
    @Query("SELECT COUNT(af) FROM AiFeedbackEntity af WHERE af.storeId = :storeId")
    Long countByStoreId(@Param("storeId") Long storeId);
}
