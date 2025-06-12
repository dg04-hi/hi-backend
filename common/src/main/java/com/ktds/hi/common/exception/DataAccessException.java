package com.ktds.hi.common.exception;

/**
 * 데이터 접근 관련 예외
 * 데이터베이스, 캐시 등 데이터 저장소 접근 시 발생하는 예외
 */
public class DataAccessException extends BusinessException {
    
    public DataAccessException(String message) {
        super("DATA_ACCESS_ERROR", message);
    }
    
    public DataAccessException(String message, Throwable cause) {
        super("DATA_ACCESS_ERROR", message, cause);
    }
    
    /**
     * 데이터베이스 연결 실패
     */
    public static DataAccessException connectionFailed() {
        return new DataAccessException("데이터베이스 연결에 실패했습니다");
    }
    
    /**
     * 데이터 정합성 오류
     */
    public static DataAccessException integrityViolation(String detail) {
        return new DataAccessException("데이터 정합성 오류: " + detail);
    }
    
    /**
     * 캐시 접근 실패
     */
    public static DataAccessException cacheAccessFailed() {
        return new DataAccessException("캐시 접근에 실패했습니다");
    }
}
