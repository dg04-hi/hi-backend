package com.ktds.hi.common.exception;

public class ExternalServiceException extends BusinessException {

    private final String serviceName;

    public ExternalServiceException(String serviceName, String message) {
        super("EXTERNAL_SERVICE_ERROR", String.format("%s 서비스 오류: %s", serviceName, message));
        this.serviceName = serviceName;
    }

    public ExternalServiceException(String serviceName, String message, Throwable cause) {
        super("EXTERNAL_SERVICE_ERROR", String.format("%s 서비스 오류: %s", serviceName, message), cause);
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public static ExternalServiceException apiCallFailed(String serviceName, String endpoint) {
        return new ExternalServiceException(serviceName, endpoint + " API 호출에 실패했습니다");
    }

    public static ExternalServiceException serviceUnavailable(String serviceName) {
        return new ExternalServiceException(serviceName, "서비스를 사용할 수 없습니다");
    }

    public static ExternalServiceException timeout(String serviceName) {
        return new ExternalServiceException(serviceName, "응답 시간이 초과되었습니다");
    }

    public static ExternalServiceException authenticationFailed(String serviceName) {
        return new ExternalServiceException(serviceName, "인증에 실패했습니다");
    }
}
