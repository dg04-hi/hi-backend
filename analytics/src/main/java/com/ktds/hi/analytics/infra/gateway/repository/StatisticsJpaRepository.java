package com.ktds.hi.analytics.infra.gateway.repository;

import com.ktds.hi.analytics.infra.gateway.entity.StatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 통계 JPA 리포지토리 인터페이스
 * 통계 데이터의 CRUD 작업을 담당
 */
@Repository
public interface StatisticsJpaRepository extends JpaRepository<StatisticsEntity, Long> {
    
    /**
     * 매장 ID와 분석 기간으로 통계 조회
     */
    List<StatisticsEntity> findByStoreIdAndAnalysisDateBetween(
            Long storeId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 매장 ID와 특정 날짜로 통계 조회
     */
    Optional<StatisticsEntity> findByStoreIdAndAnalysisDate(Long storeId, LocalDate analysisDate);
    
    /**
     * 매장 ID로 최신 통계 조회
     */
    Optional<StatisticsEntity> findTopByStoreIdOrderByAnalysisDateDesc(Long storeId);
}
