package com.ktds.hi.common.exception;

public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String resourceType, String field, Object value) {
        super("DUPLICATE_RESOURCE", String.format("이미 존재하는 %s입니다. %s: %s", resourceType, field, value));
    }

    public DuplicateResourceException(String message) {
        super("DUPLICATE_RESOURCE", message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super("DUPLICATE_RESOURCE", message, cause);
    }

    public static DuplicateResourceException username(String username) {
        return new DuplicateResourceException("사용자", "사용자명", username);
    }

    public static DuplicateResourceException email(String email) {
        return new DuplicateResourceException("사용자", "이메일", email);
    }

    public static DuplicateResourceException nickname(String nickname) {
        return new DuplicateResourceException("사용자", "닉네임", nickname);
    }

    public static DuplicateResourceException phone(String phone) {
        return new DuplicateResourceException("사용자", "전화번호", phone);
    }

    public static DuplicateResourceException storeName(String storeName) {
        return new DuplicateResourceException("매장", "매장명", storeName);
    }
}
