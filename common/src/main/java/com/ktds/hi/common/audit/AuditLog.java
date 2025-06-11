package com.ktds.hi.common.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 감사 로그 엔티티
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    private Long id;
    private String entityType;
    private String entityId;
    private AuditAction action;
    private String oldValues;
    private String newValues;
    private String userId;
    private String userAgent;
    private String ipAddress;
    private LocalDateTime timestamp;
}