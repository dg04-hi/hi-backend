package com.ktds.hi.analytics.infra.gateway;

import com.ktds.hi.analytics.biz.domain.ActionPlan;
import com.ktds.hi.analytics.biz.usecase.out.EventPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 이벤트 어댑터 클래스
 * Event Port를 구현하여 이벤트 발행 기능을 제공
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventAdapter implements EventPort {
    
    private final ApplicationEventPublisher eventPublisher;
    
    @Override
    public void publishActionPlanCreatedEvent(ActionPlan actionPlan) {
        log.info("실행 계획 생성 이벤트 발행: planId={}, storeId={}", actionPlan.getId(), actionPlan.getStoreId());
        
        try {
            // 실행 계획 생성 이벤트 객체 생성 및 발행
            ActionPlanCreatedEvent event = new ActionPlanCreatedEvent(actionPlan);
            eventPublisher.publishEvent(event);
            
            log.info("실행 계획 생성 이벤트 발행 완료: planId={}", actionPlan.getId());
            
        } catch (Exception e) {
            log.error("실행 계획 생성 이벤트 발행 실패: planId={}, error={}", actionPlan.getId(), e.getMessage(), e);
        }
    }
    
    /**
     * 실행 계획 생성 이벤트 클래스
     */
    public static class ActionPlanCreatedEvent {
        private final ActionPlan actionPlan;
        
        public ActionPlanCreatedEvent(ActionPlan actionPlan) {
            this.actionPlan = actionPlan;
        }
        
        public ActionPlan getActionPlan() {
            return actionPlan;
        }
    }
}
