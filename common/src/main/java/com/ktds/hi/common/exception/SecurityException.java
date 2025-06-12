package com.ktds.hi.common.exception;

/**
 * 보안 관련 예외
 * 인증, 인가 외의 보안 관련 예외
 */
public class SecurityException extends BusinessException {
    
    public SecurityException(String message) {
        super("SECURITY_ERROR", message);
    }
    
    public SecurityException(String message, Throwable cause) {
        super("SECURITY_ERROR", message, cause);
    }
    
    /**
     * 보안 정책 위반
     */
    public static SecurityException policyViolation(String policy) {
        return new SecurityException("보안 정책 위반: " + policy);
    }
    
    /**
     * 의심스러운 활동 감지
     */
    public static SecurityException suspiciousActivity(String activity) {
        return new SecurityException("의심스러운 활동이 감지되었습니다: " + activity);
    }
    
    /**
     * IP 차단
     */
    public static SecurityException ipBlocked(String ip) {
        return new SecurityException("차단된 IP 주소입니다: " + ip);
    }
}
