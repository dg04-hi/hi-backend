package com.ktds.hi.common.service;

/**
 * 감사 액션 열거형
 * 시스템에서 발생하는 주요 액션들을 정의
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
public enum AuditAction {

    /**
     * 생성 액션
     */
    CREATE("생성"),

    /**
     * 수정 액션
     */
    UPDATE("수정"),

    /**
     * 삭제 액션
     */
    DELETE("삭제"),

    /**
     * 조회 액션
     */
    ACCESS("조회"),

    /**
     * 로그인 액션
     */
    LOGIN("로그인"),

    /**
     * 로그아웃 액션
     */
    LOGOUT("로그아웃"),

    /**
     * 승인 액션
     */
    APPROVE("승인"),

    /**
     * 거부 액션
     */
    REJECT("거부"),

    /**
     * 활성화 액션
     */
    ACTIVATE("활성화"),

    /**
     * 비활성화 액션
     */
    DEACTIVATE("비활성화");

    private final String description;

    AuditAction(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}