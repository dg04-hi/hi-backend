package com.ktds.hi.common.exception;

public class AuthorizationException extends BusinessException {

    public AuthorizationException(String message) {
        super("AUTHORIZATION_ERROR", message);
    }

    public AuthorizationException(String message, Object... args) {
        super("AUTHORIZATION_ERROR", message, args);
    }

    public AuthorizationException(String message, Throwable cause) {
        super("AUTHORIZATION_ERROR", message, cause);
    }

    public static AuthorizationException accessDenied() {
        return new AuthorizationException("접근 권한이 없습니다");
    }

    public static AuthorizationException accessDenied(String resource) {
        return new AuthorizationException(resource + "에 대한 접근 권한이 없습니다");
    }

    public static AuthorizationException insufficientRole(String requiredRole) {
        return new AuthorizationException(requiredRole + " 권한이 필요합니다");
    }

    public static AuthorizationException ownershipRequired() {
        return new AuthorizationException("본인만 접근할 수 있습니다");
    }
}
