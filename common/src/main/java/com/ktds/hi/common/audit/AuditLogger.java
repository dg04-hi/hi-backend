package com.ktds.hi.common.audit;

import com.ktds.hi.common.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;


/**
 * 감사 로거
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogger {

    private final AuditLogRepository auditLogRepository;

    public void logCreate(Object entity) {
        AuditLog auditLog = AuditLog.builder()
                .entityType(entity.getClass().getSimpleName())
                .entityId(extractEntityId(entity))
                .action(AuditAction.CREATE)
                .newValues(extractEntityInfo(entity))
                .userId(getCurrentUserInfo())
                .ipAddress(getClientInfo())
                .timestamp(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);
        log.info("감사 로그 기록 - CREATE: {}", auditLog);
    }

    public void logUpdate(Object entity, Map<String, Object> oldValues, Map<String, Object> newValues) {
        AuditLog auditLog = AuditLog.builder()
                .entityType(entity.getClass().getSimpleName())
                .entityId(extractEntityId(entity))
                .action(AuditAction.UPDATE)
                .oldValues(oldValues.toString())
                .newValues(newValues.toString())
                .userId(getCurrentUserInfo())
                .ipAddress(getClientInfo())
                .timestamp(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);
        log.info("감사 로그 기록 - UPDATE: {}", auditLog);
    }

    public void logDelete(Object entity) {
        AuditLog auditLog = AuditLog.builder()
                .entityType(entity.getClass().getSimpleName())
                .entityId(extractEntityId(entity))
                .action(AuditAction.DELETE)
                .oldValues(extractEntityInfo(entity))
                .userId(getCurrentUserInfo())
                .ipAddress(getClientInfo())
                .timestamp(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);
        log.info("감사 로그 기록 - DELETE: {}", auditLog);
    }

    public void logAccess(String entityType, String entityId) {
        AuditLog auditLog = AuditLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .action(AuditAction.ACCESS)
                .userId(getCurrentUserInfo())
                .ipAddress(getClientInfo())
                .timestamp(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);
        log.debug("감사 로그 기록 - ACCESS: {}", auditLog);
    }

    private String extractEntityId(Object entity) {
        // 리플렉션을 사용하여 ID 필드 추출
        try {
            return entity.getClass().getMethod("getId").invoke(entity).toString();
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String extractEntityInfo(Object entity) {
        // 엔티티 정보를 JSON 형태로 변환
        return entity.toString();
    }

    private String getCurrentUserInfo() {
        // 현재 사용자 정보 반환
        return "system"; // 실제 구현에서는 SecurityContext에서 가져옴
    }

    private String getClientInfo() {
        // 클라이언트 IP 정보 반환
        return "127.0.0.1"; // 실제 구현에서는 HttpServletRequest에서 가져옴
    }
}
