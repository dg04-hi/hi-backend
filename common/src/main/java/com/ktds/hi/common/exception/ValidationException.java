package com.ktds.hi.common.exception;

public class ValidationException extends BusinessException {

    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }

    public ValidationException(String message, Object... args) {
        super("VALIDATION_ERROR", message, args);
    }

    public ValidationException(String field, String message) {
        super("VALIDATION_ERROR", String.format("%s: %s", field, message));
    }

    public ValidationException(String message, Throwable cause) {
        super("VALIDATION_ERROR", message, cause);
    }

    public static ValidationException requiredField(String fieldName) {
        return new ValidationException(fieldName + "은(는) 필수 입력 항목입니다");
    }

    public static ValidationException invalidFormat(String fieldName) {
        return new ValidationException(fieldName + "의 형식이 올바르지 않습니다");
    }

    public static ValidationException outOfRange(String fieldName, Object min, Object max) {
        return new ValidationException(String.format("%s는 %s와 %s 사이의 값이어야 합니다", fieldName, min, max));
    }

    public static ValidationException lengthExceeded(String fieldName, int maxLength) {
        return new ValidationException(String.format("%s는 %d자 이하로 입력해주세요", fieldName, maxLength));
    }
}
