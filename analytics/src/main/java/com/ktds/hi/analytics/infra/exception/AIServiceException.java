package com.ktds.hi.analytics.infra.exception;

/**
 * AI 서비스 연동 중 발생하는 예외
 */
public class AIServiceException extends AnalyticsException {
    
    public AIServiceException(String message) {
        super("AI_SERVICE_ERROR", message);
    }
    
    public AIServiceException(String message, Throwable cause) {
        super("AI_SERVICE_ERROR", message, cause);
    }
}
