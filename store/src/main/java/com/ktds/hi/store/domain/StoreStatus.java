package com.ktds.hi.store.domain;

/**
 * 매장 상태 열거형
 * 매장의 운영 상태를 정의
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
public enum StoreStatus {

    /**
     * 활성 상태 - 정상 운영 중
     */
    ACTIVE("활성"),

    /**
     * 비활성 상태 - 임시 휴업
     */
    INACTIVE("비활성"),

    /**
     * 일시 정지 상태 - 관리자에 의한 일시 정지
     */
    SUSPENDED("일시정지"),

    /**
     * 삭제 상태 - 영구 삭제 (소프트 삭제)
     */
    DELETED("삭제");

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
            return INACTIVE;
        }

        try {
            return StoreStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return INACTIVE;
        }
    }

    /**
     * 매장이 서비스 가능한 상태인지 확인
     */
    public boolean isServiceable() {
        return this == ACTIVE;
    }

    /**
     * 매장이 삭제된 상태인지 확인
     */
    public boolean isDeleted() {
        return this == DELETED;
    }
}