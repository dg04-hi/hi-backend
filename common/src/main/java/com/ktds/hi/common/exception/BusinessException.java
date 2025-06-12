// common/src/main/java/com/ktds/hi/common/exception/BusinessException.java
package com.ktds.hi.common.exception;

/**
 * 비즈니스 로직 예외의 기본 클래스
 * 모든 커스텀 예외의 부모 클래스
 */
public class BusinessException extends RuntimeException {

    private String errorCode;
    private Object[] args;

    /**
     * 메시지만으로 예외 생성
     */
    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
    }

    /**
     * 메시지와 원인으로 예외 생성
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BUSINESS_ERROR";
    }

    /**
     * 에러 코드와 메시지로 예외 생성
     */
    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 에러 코드, 메시지, 파라미터로 예외 생성
     */
    public BusinessException(String errorCode, String message, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.args = args;
    }

    /**
     * 에러 코드, 메시지, 원인으로 예외 생성
     */
    public BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * 에러 코드 반환
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 메시지 파라미터 반환
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * 정적 팩토리 메서드 - 일반적인 비즈니스 예외
     */
    public static BusinessException of(String message) {
        return new BusinessException(message);
    }

    /**
     * 정적 팩토리 메서드 - 에러 코드와 메시지
     */
    public static BusinessException of(String errorCode, String message) {
        return new BusinessException(errorCode, message);
    }

    /**
     * 정적 팩토리 메서드 - 원인 포함
     */
    public static BusinessException of(String message, Throwable cause) {
        return new BusinessException(message, cause);
    }
}