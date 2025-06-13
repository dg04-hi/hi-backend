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
}