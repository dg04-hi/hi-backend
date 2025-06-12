package com.ktds.hi.analytics.biz.usecase.out;

import com.ktds.hi.analytics.biz.domain.ActionPlan;
import com.ktds.hi.analytics.biz.domain.AnalysisType;

/**
 * 이벤트 포트 인터페이스
 * 이벤트 발행을 위한 출력 포트
 */
public interface EventPort {
    
    /**
     * 분석 완료 이벤트 발행
     */
    void publishAnalysisCompletedEvent(Long storeId, AnalysisType analysisType);
    
    /**
     * 실행 계획 생성 이벤트 발행
     */
    void publishActionPlanCreatedEvent(ActionPlan actionPlan);
}
