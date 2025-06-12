package com.ktds.hi.common.exception;

public class DataIntegrityException extends BusinessException {

    public DataIntegrityException(String message) {
        super("DATA_INTEGRITY_ERROR", message);
    }

    public DataIntegrityException(String message, Throwable cause) {
        super("DATA_INTEGRITY_ERROR", message, cause);
    }

    public static DataIntegrityException foreignKeyViolation(String entity, Object id) {
        return new DataIntegrityException(String.format("%s(ID: %s)에 참조된 데이터가 있어 삭제할 수 없습니다", entity, id));
    }

    public static DataIntegrityException uniqueConstraintViolation(String field, Object value) {
        return new DataIntegrityException(String.format("중복된 값입니다. %s: %s", field, value));
    }

    public static DataIntegrityException notNullViolation(String field) {
        return new DataIntegrityException(field + "은(는) 필수 값입니다");
    }
}
