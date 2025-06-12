package com.ktds.hi.analytics.infra.exception;

/**
 * 실행 계획을 찾을 수 없을 때 발생하는 예외
 */
public class ActionPlanNotFoundException extends AnalyticsException {
    
    public ActionPlanNotFoundException(Long planId) {
        super("ACTION_PLAN_NOT_FOUND", "실행 계획을 찾을 수 없습니다: " + planId);
    }

    public ActionPlanNotFoundException(String message) {
        super("ACTION_PLAN_NOT_FOUND", message);
    }
}
