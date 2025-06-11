package com.ktds.hi.common.audit;

/**
 * 감사 로그 리포지토리 인터페이스
 */
public interface AuditLogRepository {

    void save(AuditLog auditLog);

    AuditLog findById(Long id);
}