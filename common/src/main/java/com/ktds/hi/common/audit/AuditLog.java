package com.ktds.hi.common.audit;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 감사 로그 엔티티
 */
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AuditLog {

    @Id
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
    private LocalDateTime createdAt;



}