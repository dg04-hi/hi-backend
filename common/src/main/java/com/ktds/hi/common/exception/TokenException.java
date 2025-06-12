package com.ktds.hi.common.exception;

/**
 * JWT 토큰 관련 예외
 * 토큰 만료, 유효하지 않은 토큰 등에 사용
 */
public class TokenException extends BusinessException {
    
    public TokenException(String message) {
        super("TOKEN_ERROR", message);
    }
    
    public TokenException(String message, Throwable cause) {
        super("TOKEN_ERROR", message, cause);
    }
    
    /**
     * 토큰 만료 예외
     */
    public static TokenException expired() {
        return new TokenException("토큰이 만료되었습니다");
    }
    
    /**
     * 토큰 형식 오류 예외
     */
    public static TokenException invalid() {
        return new TokenException("유효하지 않은 토큰입니다");
    }
    
    /**
     * 토큰 누락 예외
     */
    public static TokenException missing() {
        return new TokenException("토큰이 필요합니다");
    }
    
    /**
     * 잘못된 토큰 타입 예외
     */
    public static TokenException wrongType(String expectedType) {
        return new TokenException(expectedType + " 토큰이 필요합니다");
    }
}
