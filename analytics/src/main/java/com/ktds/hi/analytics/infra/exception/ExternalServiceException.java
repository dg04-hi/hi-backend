package com.ktds.hi.analytics.infra.exception;

/**
 * 외부 서비스 연동 중 발생하는 예외
 */
public class ExternalServiceException extends AnalyticsException {
    
    public ExternalServiceException(String serviceName, String message) {
        super("EXTERNAL_SERVICE_ERROR", serviceName + " 서비스 오류: " + message);
    }
    
    public ExternalServiceException(String serviceName, String message, Throwable cause) {
        super("EXTERNAL_SERVICE_ERROR", serviceName + " 서비스 오류: " + message, cause);
    }
}
