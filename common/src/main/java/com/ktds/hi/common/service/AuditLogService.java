package com.ktds.hi.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 감사 로그 서비스
 * 시스템 내 중요한 작업들을 로깅
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Service
@Slf4j
public class AuditLogService {

    /**
     * 비동기 로그 기록
     */
    @Async
    public void logAsync(AuditAction action, String entityType, String entityId, String description) {
        try {
            // 현재 사용자 정보 가져오기 (SecurityContext에서)
            String userId = getCurrentUserId();
            String username = getCurrentUsername();

            // 감사 로그 생성 및 저장
            log.info("AUDIT_LOG: action={}, entityType={}, entityId={}, userId={}, username={}, description={}",
                    action, entityType, entityId, userId, username, description);

            // 실제 환경에서는 데이터베이스에 저장
            // AuditLog auditLog = AuditLog.create(userId, username, action, entityType, entityId, description);
            // auditLogRepository.save(auditLog);

        } catch (Exception e) {
            log.error("Failed to save audit log: action={}, entityType={}, entityId={}",
                    action, entityType, entityId, e);
        }
    }

    /**
     * 생성 로그
     */
    public void logCreate(String entityType, String entityId, String description) {
        logAsync(AuditAction.CREATE, entityType, entityId, description);
    }

    /**
     * 수정 로그
     */
    public void logUpdate(String entityType, String entityId, String description) {
        logAsync(AuditAction.UPDATE, entityType, entityId, description);
    }

    /**
     * 삭제 로그
     */
    public void logDelete(String entityType, String entityId, String description) {
        logAsync(AuditAction.DELETE, entityType, entityId, description);
    }

    /**
     * 접근 로그
     */
    public void logAccess(String entityType, String entityId, String description) {
        logAsync(AuditAction.ACCESS, entityType, entityId, description);
    }

    /**
     * 로그인 로그
     */
    public void logLogin(String description) {
        logAsync(AuditAction.LOGIN, "USER", getCurrentUserId(), description);
    }

    /**
     * 로그아웃 로그
     */
    public void logLogout(String description) {
        logAsync(AuditAction.LOGOUT, "USER", getCurrentUserId(), description);
    }

    /**
     * 현재 사용자 ID 조회
     */
    private String getCurrentUserId() {
        try {
            // SecurityContext에서 사용자 ID 추출
            // 실제 구현에서는 SecurityContextHolder 사용
            return "SYSTEM"; // 임시값
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    /**
     * 현재 사용자명 조회
     */
    private String getCurrentUsername() {
        try {
            // SecurityContext에서 사용자명 추출
            return "system"; // 임시값
        } catch (Exception e) {
            return "unknown";
        }
    }
}