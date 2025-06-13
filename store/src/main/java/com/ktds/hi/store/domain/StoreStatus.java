package com.ktds.hi.store.domain;

/**
 * 매장 상태 열거형
 */
public enum StoreStatus {
    ACTIVE("활성"),
    INACTIVE("비활성"),
    DELETED("삭제됨"),
    PENDING("승인대기");

    private final String description;

    StoreStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 문자열로부터 StoreStatus 변환
     */
    public static StoreStatus fromString(String status) {
        if (status == null) {
            return ACTIVE; // 기본값
        }

        try {
            return StoreStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ACTIVE; // 기본값
        }
    }
}