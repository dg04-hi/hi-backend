package com.ktds.hi.analytics.biz.usecase.out;

import com.ktds.hi.analytics.biz.domain.ActionPlan;

import java.util.List;
import java.util.Optional;

/**
 * 실행 계획 포트 인터페이스
 * Clean Architecture의 출력 포트 정의
 */
public interface ActionPlanPort {
    
    /**
     * 매장 ID로 실행 계획 목록 조회
     */
    List<ActionPlan> findActionPlansByStoreId(Long storeId);
    
    /**
     * 실행 계획 ID로 조회
     */
    Optional<ActionPlan> findActionPlanById(Long planId);
    
    /**
     * 실행 계획 저장
     */
    ActionPlan saveActionPlan(ActionPlan actionPlan);
    
    /**
     * 실행 계획 삭제
     */
    void deleteActionPlan(Long planId);
}
