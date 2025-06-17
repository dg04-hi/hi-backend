package com.ktds.hi.analytics.infra.gateway.repository;

import com.ktds.hi.analytics.biz.domain.PlanStatus;
import com.ktds.hi.analytics.infra.gateway.entity.ActionPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import io.lettuce.core.dynamic.annotation.Param;

/**
 * 실행 계획 JPA 리포지토리 인터페이스
 * 실행 계획 데이터의 CRUD 작업을 담당
 */
@Repository
public interface ActionPlanJpaRepository extends JpaRepository<ActionPlanEntity, Long> {
    
    /**
     * 매장 ID로 실행 계획 목록 조회 (최신순)
     */
    List<ActionPlanEntity> findByStoreIdOrderByCreatedAtDesc(Long storeId);
    
    /**
     * 매장 ID와 상태로 실행 계획 목록 조회 (최신순)
     */
    List<ActionPlanEntity> findByStoreIdAndStatusOrderByCreatedAtDesc(Long storeId, PlanStatus status);
    
    /**
     * 사용자 ID로 실행 계획 목록 조회
     */
    List<ActionPlanEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 매장 ID와 사용자 ID로 실행 계획 목록 조회
     */
    List<ActionPlanEntity> findByStoreIdAndUserIdOrderByCreatedAtDesc(Long storeId, Long userId);

    /**
     * 피드백 id로 실행계획 title 조회
     */
    @Query("SELECT a.title FROM ActionPlanEntity a WHERE a.feedbackId = :feedbackId")
    List<String> findActionPlanTitleByFeedbackId(@Param("feedbackId")Long feedbackId);
}
