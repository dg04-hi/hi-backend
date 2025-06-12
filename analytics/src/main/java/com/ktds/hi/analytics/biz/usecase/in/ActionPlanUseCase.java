package com.ktds.hi.analytics.biz.usecase.in;

import com.ktds.hi.analytics.infra.dto.*;

import java.util.List;

/**
 * 실행 계획 UseCase 인터페이스
 * Clean Architecture의 입력 포트 정의
 */
public interface ActionPlanUseCase {
    
    /**
     * 실행 계획 목록 조회
     */
    List<ActionPlanListResponse> getActionPlans(Long storeId);
    
    /**
     * 실행 계획 상세 조회
     */
    ActionPlanDetailResponse getActionPlanDetail(Long planId);
    
    /**
     * 실행 계획 저장
     */
    ActionPlanSaveResponse saveActionPlan(ActionPlanSaveRequest request);
    
    /**
     * 실행 계획 완료 처리
     */
    ActionPlanCompleteResponse completeActionPlan(Long planId, ActionPlanCompleteRequest request);
    
    /**
     * 실행 계획 삭제
     */
    ActionPlanDeleteResponse deleteActionPlan(Long planId);
}
