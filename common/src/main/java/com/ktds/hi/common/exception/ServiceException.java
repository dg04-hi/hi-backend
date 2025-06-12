package com.ktds.hi.common.exception;

/**
 * 서비스 레이어 예외
 * 비즈니스 로직 실행 중 발생하는 일반적인 예외
 */
public class ServiceException extends BusinessException {
    
    public ServiceException(String message) {
        super("SERVICE_ERROR", message);
    }
    
    public ServiceException(String message, Throwable cause) {
        super("SERVICE_ERROR", message, cause);
    }
    
    public ServiceException(String errorCode, String message) {
        super(errorCode, message);
    }
    
    /**
     * 서비스 일시 중단
     */
    public static ServiceException temporarilyUnavailable(String serviceName) {
        return new ServiceException("서비스가 일시적으로 이용할 수 없습니다: " + serviceName);
    }
    
    /**
     * 처리 한도 초과
     */
    public static ServiceException limitExceeded(String limitType) {
        return new ServiceException("처리 한도를 초과했습니다: " + limitType);
    }
}
