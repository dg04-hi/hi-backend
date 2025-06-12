package com.ktds.hi.common.audit;

import com.ktds.hi.common.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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


    /**
     * 생성 작업 감사 로그 기록
     */
    public void logCreate(Object entity) {
        try {
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
            log.debug("감사 로그 기록 - CREATE: {}", auditLog);
        } catch (Exception e) {
            log.warn("Failed to audit create operation", e);
        }
    }

    /**
     * 수정 작업 감사 로그 기록
     */
    public void logUpdate(Object entity, Map<String, Object> oldValues, Map<String, Object> newValues) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .entityType(entity.getClass().getSimpleName())
                    .entityId(extractEntityId(entity))
                    .action(AuditAction.UPDATE)
                    .oldValues(oldValues != null ? oldValues.toString() : null)
                    .newValues(newValues != null ? newValues.toString() : null)
                    .userId(getCurrentUserInfo())
                    .ipAddress(getClientInfo())
                    .timestamp(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("감사 로그 기록 - UPDATE: {}", auditLog);
        } catch (Exception e) {
            log.warn("Failed to audit update operation", e);
        }
    }

    /**
     * 삭제 작업 감사 로그 기록
     */
    public void logDelete(Object entity) {
        try {
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
            log.debug("감사 로그 기록 - DELETE: {}", auditLog);
        } catch (Exception e) {
            log.warn("Failed to audit delete operation", e);
        }
    }

    /**
     * 접근 작업 감사 로그 기록
     */
    public void logAccess(String entityType, String entityId) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .entityType(entityType)
                    .entityId(entityId)
                    .action(AuditAction.VIEW)
                    .userId(getCurrentUserInfo())
                    .ipAddress(getClientInfo())
                    .timestamp(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("감사 로그 기록 - ACCESS: {}", auditLog);
        } catch (Exception e) {
            log.warn("Failed to audit access operation", e);
        }
    }



    /**
     * 정적 create 메서드 (AuditLogService와의 호환성을 위해 추가)
     */
    public static AuditLog create(Long userId, String username, AuditAction action,
                                  String entityType, String entityId, String description) {
        return AuditLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .newValues(description)
                .userId(username)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 엔티티 ID 추출
     */
    private String extractEntityId(Object entity) {
        if (entity == null) {
            return "UNKNOWN";
        }

        try {
            // 리플렉션을 사용하여 getId() 메서드 호출
            var method = entity.getClass().getMethod("getId");
            Object id = method.invoke(entity);
            return id != null ? id.toString() : "UNKNOWN";
        } catch (Exception e) {
            log.debug("Failed to extract entity ID from {}", entity.getClass().getSimpleName(), e);
            return "UNKNOWN";
        }
    }

    /**
     * 엔티티 정보를 문자열로 변환
     */
    private String extractEntityInfo(Object entity) {
        if (entity == null) {
            return null;
        }

        try {
            // 간단한 toString() 사용 (필요시 JSON 변환 로직으로 교체 가능)
            return entity.toString();
        } catch (Exception e) {
            log.debug("Failed to extract entity info from {}", entity.getClass().getSimpleName(), e);
            return entity.getClass().getSimpleName() + "@" + System.identityHashCode(entity);
        }
    }

    /**
     * 현재 사용자 정보 반환
     */
    private String getCurrentUserInfo() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())) {
                return authentication.getName();
            }
        } catch (Exception e) {
            log.debug("Failed to get current user info", e);
        }
        return "SYSTEM";
    }

    /**
     * 클라이언트 정보 (IP 주소) 반환
     */
    private String getClientInfo() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            // X-Forwarded-For 헤더 확인 (프록시/로드밸런서 환경)
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }

            // X-Real-IP 헤더 확인
            String xRealIp = request.getHeader("X-Real-IP");
            if (xRealIp != null && !xRealIp.isEmpty()) {
                return xRealIp;
            }

            // 기본 원격 주소
            return request.getRemoteAddr();
        } catch (Exception e) {
            log.debug("Failed to get client info", e);
            return "127.0.0.1";
        }
    }
}