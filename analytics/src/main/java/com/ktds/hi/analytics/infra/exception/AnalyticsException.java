package com.ktds.hi.analytics.infra.exception;

/**
 * 분석 서비스 커스텀 예외 클래스
 */
public class AnalyticsException extends RuntimeException {
    
    private final String errorCode;
    
    public AnalyticsException(String message) {
        super(message);
        this.errorCode = "ANALYTICS_ERROR";
    }
    
    public AnalyticsException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public AnalyticsException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "ANALYTICS_ERROR";
    }
    
    public AnalyticsException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
