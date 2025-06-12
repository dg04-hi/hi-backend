package com.ktds.hi.common.exception;

/**
 * 동시성 관련 예외
 * 동시 접근, 락 충돌 등에서 발생하는 예외
 */
public class ConcurrencyException extends BusinessException {
    
    public ConcurrencyException(String message) {
        super("CONCURRENCY_ERROR", message);
    }
    
    public ConcurrencyException(String message, Throwable cause) {
        super("CONCURRENCY_ERROR", message, cause);
    }
    
    /**
     * 낙관적 락 충돌
     */
    public static ConcurrencyException optimisticLockConflict() {
        return new ConcurrencyException("동시 수정으로 인한 충돌이 발생했습니다. 다시 시도해주세요");
    }
    
    /**
     * 비관적 락 타임아웃
     */
    public static ConcurrencyException lockTimeout() {
        return new ConcurrencyException("리소스 잠금 시간이 초과되었습니다");
    }
    
    /**
     * 데드락 발생
     */
    public static ConcurrencyException deadlockDetected() {
        return new ConcurrencyException("데드락이 감지되었습니다");
    }
}
