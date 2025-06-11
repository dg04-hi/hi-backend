package com.ktds.hi.analytics.infra.gateway.repository;

import com.ktds.hi.analytics.infra.gateway.entity.AiFeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * AI 피드백 JPA 리포지토리 인터페이스
 * AI 피드백 데이터의 CRUD 작업을 담당
 */
@Repository
public interface AiFeedbackJpaRepository extends JpaRepository<AiFeedbackEntity, Long> {
    
    /**
     * 매장 ID와 분석 기간으로 AI 피드백 목록 조회
     */
    List<AiFeedbackEntity> findByStoreIdAndAnalysisDateBetweenOrderByAnalysisDateDesc(
            Long storeId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 매장 ID로 최신 AI 피드백 조회
     */
    AiFeedbackEntity findTopByStoreIdOrderByCreatedAtDesc(Long storeId);
    
    /**
     * 특정 날짜의 AI 피드백 조회
     */
    List<AiFeedbackEntity> findByStoreIdAndAnalysisDate(Long storeId, LocalDate analysisDate);
}
