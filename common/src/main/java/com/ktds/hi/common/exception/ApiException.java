package com.ktds.hi.common.exception;

import org.springframework.http.HttpStatus;

/**
 * API 관련 예외
 * HTTP 상태 코드와 함께 사용되는 예외
 */
public class ApiException extends BusinessException {
    
    private final HttpStatus httpStatus;
    
    public ApiException(HttpStatus httpStatus, String message) {
        super(httpStatus.name(), message);
        this.httpStatus = httpStatus;
    }
    
    public ApiException(HttpStatus httpStatus, String message, Throwable cause) {
        super(httpStatus.name(), message, cause);
        this.httpStatus = httpStatus;
    }
    
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
    
    /**
     * Bad Request (400)
     */
    public static ApiException badRequest(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, message);
    }
    
    /**
     * Unauthorized (401)
     */
    public static ApiException unauthorized(String message) {
        return new ApiException(HttpStatus.UNAUTHORIZED, message);
    }
    
    /**
     * Forbidden (403)
     */
    public static ApiException forbidden(String message) {
        return new ApiException(HttpStatus.FORBIDDEN, message);
    }
    
    /**
     * Not Found (404)
     */
    public static ApiException notFound(String message) {
        return new ApiException(HttpStatus.NOT_FOUND, message);
    }
    
    /**
     * Method Not Allowed (405)
     */
    public static ApiException methodNotAllowed(String message) {
        return new ApiException(HttpStatus.METHOD_NOT_ALLOWED, message);
    }
    
    /**
     * Conflict (409)
     */
    public static ApiException conflict(String message) {
        return new ApiException(HttpStatus.CONFLICT, message);
    }
    
    /**
     * Internal Server Error (500)
     */
    public static ApiException internalServerError(String message) {
        return new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
    
    /**
     * Service Unavailable (503)
     */
    public static ApiException serviceUnavailable(String message) {
        return new ApiException(HttpStatus.SERVICE_UNAVAILABLE, message);
    }
}
