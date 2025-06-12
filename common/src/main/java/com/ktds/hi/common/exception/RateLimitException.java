package com.ktds.hi.common.exception;

/**
 * 요청 제한 관련 예외
 * API 호출 제한, 처리량 제한 등에서 발생하는 예외
 */
public class RateLimitException extends BusinessException {
    
    private final long retryAfter; // 재시도 가능 시간 (초)
    
    public RateLimitException(String message, long retryAfter) {
        super("RATE_LIMIT_EXCEEDED", message);
        this.retryAfter = retryAfter;
    }
    
    public RateLimitException(String message) {
        this(message, 60); // 기본값: 60초 후 재시도
    }
    
    public long getRetryAfter() {
        return retryAfter;
    }
    
    /**
     * API 호출 제한 초과
     */
    public static RateLimitException apiCallLimitExceeded(long retryAfter) {
        return new RateLimitException("API 호출 제한을 초과했습니다. 잠시 후 다시 시도해주세요", retryAfter);
    }
    
    /**
     * 동시 요청 제한 초과
     */
    public static RateLimitException concurrentRequestLimitExceeded() {
        return new RateLimitException("동시 요청 제한을 초과했습니다", 5);
    }
}
