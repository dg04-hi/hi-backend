package com.ktds.hi.common.exception;

/**
 * 설정 관련 예외
 * 애플리케이션 설정 오류, 환경 변수 누락 등에서 발생하는 예외
 */
public class ConfigurationException extends BusinessException {
    
    public ConfigurationException(String message) {
        super("CONFIGURATION_ERROR", message);
    }
    
    public ConfigurationException(String message, Throwable cause) {
        super("CONFIGURATION_ERROR", message, cause);
    }
    
    /**
     * 필수 설정 누락
     */
    public static ConfigurationException missingProperty(String propertyName) {
        return new ConfigurationException("필수 설정이 누락되었습니다: " + propertyName);
    }
    
    /**
     * 잘못된 설정 값
     */
    public static ConfigurationException invalidValue(String propertyName, String value) {
        return new ConfigurationException(String.format("잘못된 설정 값입니다. %s: %s", propertyName, value));
    }
    
    /**
     * 설정 파일 로드 실패
     */
    public static ConfigurationException loadFailed(String configFile) {
        return new ConfigurationException("설정 파일 로드에 실패했습니다: " + configFile);
    }
}
