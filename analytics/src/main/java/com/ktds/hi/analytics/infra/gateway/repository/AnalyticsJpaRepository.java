package com.ktds.hi.analytics.infra.gateway.repository;

import com.ktds.hi.analytics.infra.gateway.entity.AnalyticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 분석 데이터 JPA 리포지토리 인터페이스
 * 분석 데이터의 CRUD 작업을 담당
 */
@Repository
public interface AnalyticsJpaRepository extends JpaRepository<AnalyticsEntity, Long> {
    
    /**
     * 매장 ID로 분석 데이터 조회
     */
    Optional<AnalyticsEntity> findByStoreId(Long storeId);
    
    /**
     * 매장 ID로 최신 분석 데이터 조회
     */
    @Query("SELECT a FROM AnalyticsEntity a WHERE a.storeId = :storeId ORDER BY a.lastAnalysisDate DESC")
    Optional<AnalyticsEntity> findLatestByStoreId(@Param("storeId") Long storeId);
    
    /**
     * 특정 기간 이후 분석된 매장 목록 조회
     */
    @Query("SELECT a FROM AnalyticsEntity a WHERE a.lastAnalysisDate >= :afterDate ORDER BY a.lastAnalysisDate DESC")
    List<AnalyticsEntity> findByLastAnalysisDateAfter(@Param("afterDate") LocalDateTime afterDate);
    
    /**
     * 평균 평점이 특정 값 이하인 매장 조회
     */
    @Query("SELECT a FROM AnalyticsEntity a WHERE a.averageRating <= :rating ORDER BY a.averageRating ASC")
    List<AnalyticsEntity> findByAverageRatingLessThanEqual(@Param("rating") Double rating);
    
    /**
     * 부정 리뷰 비율이 높은 매장 조회
     */
    @Query("SELECT a FROM AnalyticsEntity a WHERE a.negativeReviewRate >= :rate ORDER BY a.negativeReviewRate DESC")
    List<AnalyticsEntity> findByHighNegativeReviewRate(@Param("rate") Double rate);
}
