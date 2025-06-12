package com.ktds.hi.common.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resourceType, Object id) {
        super("RESOURCE_NOT_FOUND", String.format("%s를 찾을 수 없습니다. ID: %s", resourceType, id));
    }

    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message);
    }

    public ResourceNotFoundException(String resourceType, String field, Object value) {
        super("RESOURCE_NOT_FOUND", String.format("%s를 찾을 수 없습니다. %s: %s", resourceType, field, value));
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super("RESOURCE_NOT_FOUND", message, cause);
    }

    public static ResourceNotFoundException byId(String resourceType, Object id) {
        return new ResourceNotFoundException(resourceType, id);
    }

    public static ResourceNotFoundException user(Long userId) {
        return new ResourceNotFoundException("사용자", userId);
    }

    public static ResourceNotFoundException store(Long storeId) {
        return new ResourceNotFoundException("매장", storeId);
    }

    public static ResourceNotFoundException review(Long reviewId) {
        return new ResourceNotFoundException("리뷰", reviewId);
    }

    public static ResourceNotFoundException userByUsername(String username) {
        return new ResourceNotFoundException("사용자", "사용자명", username);
    }
}
