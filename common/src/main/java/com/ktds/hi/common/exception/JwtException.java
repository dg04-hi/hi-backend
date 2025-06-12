package com.ktds.hi.common.exception;

/**
 * JWT 처리 관련 예외
 * JWT 파싱, 서명 검증 등에서 발생하는 예외
 */
public class JwtException extends TokenException {
    
    public JwtException(String message) {
        super(message);
    }
    
    public JwtException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * JWT 서명 검증 실패
     */
    public static JwtException signatureVerificationFailed() {
        return new JwtException("JWT 서명 검증에 실패했습니다");
    }
    
    /**
     * JWT 파싱 실패
     */
    public static JwtException parsingFailed() {
        return new JwtException("JWT 파싱에 실패했습니다");
    }
    
    /**
     * JWT 클레임 누락
     */
    public static JwtException missingClaim(String claimName) {
        return new JwtException("필수 클레임이 누락되었습니다: " + claimName);
    }
}
