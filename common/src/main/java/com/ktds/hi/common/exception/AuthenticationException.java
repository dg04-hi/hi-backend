package com.ktds.hi.common.exception;

public class AuthenticationException extends BusinessException {

    public AuthenticationException(String message) {
        super("AUTHENTICATION_ERROR", message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super("AUTHENTICATION_ERROR", message, cause);
    }

    public static AuthenticationException loginFailed() {
        return new AuthenticationException("아이디 또는 비밀번호가 일치하지 않습니다");
    }

    public static AuthenticationException tokenExpired() {
        return new AuthenticationException("토큰이 만료되었습니다");
    }

    public static AuthenticationException invalidToken() {
        return new AuthenticationException("유효하지 않은 토큰입니다");
    }

    public static AuthenticationException authenticationRequired() {
        return new AuthenticationException("로그인이 필요합니다");
    }
}
