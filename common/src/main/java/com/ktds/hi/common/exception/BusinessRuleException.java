package com.ktds.hi.common.exception;

public class BusinessRuleException extends BusinessException {

    public BusinessRuleException(String message) {
        super("BUSINESS_RULE_VIOLATION", message);
    }

    public BusinessRuleException(String message, Object... args) {
        super("BUSINESS_RULE_VIOLATION", message, args);
    }

    public BusinessRuleException(String message, Throwable cause) {
        super("BUSINESS_RULE_VIOLATION", message, cause);
    }

    public static BusinessRuleException invalidStateTransition(String from, String to) {
        return new BusinessRuleException(String.format("%s 상태에서 %s 상태로 변경할 수 없습니다", from, to));
    }

    public static BusinessRuleException unauthorizedOperation(String operation) {
        return new BusinessRuleException(operation + " 작업을 수행할 권한이 없습니다");
    }

    public static BusinessRuleException timeConstraintViolation(String operation) {
        return new BusinessRuleException(operation + "의 시간 제한을 위반했습니다");
    }

    public static BusinessRuleException duplicateOperation(String operation) {
        return new BusinessRuleException("이미 " + operation + "을(를) 수행했습니다");
    }
}
