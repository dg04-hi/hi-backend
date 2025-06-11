package com.ktds.hi.common.service;

import com.ktds.hi.common.audit.AuditAction;
import com.ktds.hi.common.audit.AuditLog;
import com.ktds.hi.common.repository.AuditLogRepository;
import com.ktds.hi.common.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 감사 로그 서비스
 * 시스템의 중요한 액션들을 비동기적으로 로깅
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {
    
    private final AuditLogRepository auditLogRepository;
    
    /**
     * 감사 로그 기록 (비동기)
     */
    @Async
    @Transactional
    public void logAsync(AuditAction action, String entityType, String entityId, String description) {
        log(action, entityType, entityId, description);
    }
    
    /**
     * 감사 로그 기록 (동기)
     */
    @Transactional
    public void log(AuditAction action, String entityType, String entityId, String description) {
        try {
            Long userId = SecurityUtil.getCurrentUserId().orElse(null);
            String username = SecurityUtil.getCurrentUsername().orElse("SYSTEM");
            
            AuditLog auditLog = AuditLog.create(userId, username, action, entityType, entityId, description);
            auditLogRepository.save(auditLog);
            
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
        logAsync(AuditAction.LOGIN, "USER", SecurityUtil.getCurrentUserId().map(String::valueOf).orElse("UNKNOWN"), description);
    }
    
    /**
     * 로그아웃 로그
     */
    public void logLogout(String description) {
        logAsync(AuditAction.LOGOUT, "USER", SecurityUtil.getCurrentUserId().map(String::valueOf).orElse("UNKNOWN"), description);
    }
}
